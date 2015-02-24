package com.wiredthing.utils.actors

import com.wiredthing.utils.AppLogging

import scala.language.postfixOps
import scala.concurrent.Await
import scala.concurrent.duration._
import akka.actor.{Actor, ActorRef, Props}
import akka.util.Timeout
import akka.pattern.ask

class ItemBuffer[T](name: String, drain: ActorRef, maxItemsToBuffer: Int = 50, maxTimeToBuffer: FiniteDuration)(implicit mf: Manifest[T]) extends Actor with AppLogging {
  private implicit val ec = context.dispatcher

  private var bufferedRows: List[T] = List.empty
  private var firstReceiveTime: Long = 0

  implicit val timeout: Timeout = Timeout(20 seconds)

  private val timerPeriod: FiniteDuration = maxTimeToBuffer / 10
  private val timer = context.system.scheduler.schedule(timerPeriod, timerPeriod, self, 'maybeFlush)

  override def receive: Receive = {
    case 'maybeFlush => maybeFlush()

    case item: T =>
      logger.trace(s"ItemBuffer received item $item")
      bufferItem(item)
      maybeFlush()
      sender ! Unit
  }

  private def bufferItem(item: T): Unit = {
    if (bufferedRows.isEmpty) firstReceiveTime = System.currentTimeMillis()
    bufferedRows = item +: bufferedRows
  }

  private def maybeFlush(): Unit = if (bufferFull || timedOut) flush()

  private def bufferFull: Boolean = bufferedRows.length >= maxItemsToBuffer

  private def timedOut: Boolean = System.currentTimeMillis() - firstReceiveTime >= maxTimeToBuffer.toMillis

  private def flush(): Unit = {
    if (!bufferedRows.isEmpty) {
      Await.ready(drain ? bufferedRows, 20 seconds)
      bufferedRows = List()
    }
  }
}

object ItemBuffer {
  def props[T](name: String, drain: ActorRef, maxItemsToBuffer: Int = 50, maxTimeToBuffer: FiniteDuration = 500 milliseconds)(implicit mf: Manifest[T]) =
    Props(classOf[ItemBuffer[T]], name, drain, maxItemsToBuffer, maxTimeToBuffer, mf)
}