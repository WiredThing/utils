package com.wiredthing.utils

import scala.language.implicitConversions

case class NonBlankString private(s: String) extends AnyVal {
  override def toString = s
}

class NbsWrappedString(s: String) {
  def toNbs: Option[NonBlankString] = NonBlankString.fromString(s)
}

object NonBlankString {
  def fromString(s: String): Option[NonBlankString] = s.find(!_.isWhitespace).map(_ => new NonBlankString(s))

  implicit def toNbsWrapped(s: String) = new NbsWrappedString(s)
}
