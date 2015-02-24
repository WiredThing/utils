package com.wiredthing.utils.actors

import akka.actor.{Actor, DeadLetter}
import com.wiredthing.utils.AppLogging

class DeadLetterLogger extends Actor with AppLogging {
  def receive = {
    case d: DeadLetter => logger.debug(d.toString)
  }
}
