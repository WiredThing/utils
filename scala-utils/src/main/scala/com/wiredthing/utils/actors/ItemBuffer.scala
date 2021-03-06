package com.wiredthing.utils.actors

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.wiredthing.utils.AppLogging

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

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

  private def maybeFlush(): Unit = if (bufferFull || timedOut) Try(flush()).recover {
    case t => logger.warn("Error during flush", t)
  }

  private def bufferFull: Boolean = bufferedRows.length >= maxItemsToBuffer

  private def timedOut: Boolean = System.currentTimeMillis() - firstReceiveTime >= maxTimeToBuffer.toMillis

  private def flush(): Unit = {
    if (!bufferedRows.isEmpty) {
      val f = drain ? bufferedRows

      f.onComplete {
        case Success(_) => bufferedRows = List()
        case Failure(t) =>
          logger.error("Got error from drain", t)
          // Reset the timer to avoid spinning too quickly
          firstReceiveTime = System.currentTimeMillis()
      }

      Await.ready(f, 20 seconds)
    }
  }
}

object ItemBuffer {
  def props[T](name: String, drain: ActorRef, maxItemsToBuffer: Int = 50, maxTimeToBuffer: FiniteDuration = 500 milliseconds)(implicit mf: Manifest[T]) =
    Props(classOf[ItemBuffer[T]], name, drain, maxItemsToBuffer, maxTimeToBuffer, mf)
}