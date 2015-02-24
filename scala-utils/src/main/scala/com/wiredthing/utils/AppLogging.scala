package com.wiredthing.utils

import org.slf4j.{LoggerFactory, Logger}

trait Loggable {
  self =>
  def logger: Logger

  /*
   * This trait allows a class that is self-typed to Loggable to instantiate other classes and
   * pass on its logger implementation
   */
  trait Log extends Loggable {
    def logger = self.logger
  }

}

trait AppLogging extends Loggable {
  val logger = LoggerFactory.getLogger("application")
}
