package com.wiredthing.utils

import scala.language.implicitConversions

case class DateMillis private(milliseconds: Long) extends AnyVal {
  override def toString = milliseconds.toString

}


object DateMillis {
  implicit def toDateMillis(milliseconds: Long) = new DateMillis(milliseconds)

}
