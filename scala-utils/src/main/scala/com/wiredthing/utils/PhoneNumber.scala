package com.wiredthing.utils

case class PhoneNumber private[utils](s: String) {
  def normalized: PhoneNumber = PhoneNumber(PhoneNumber.normalize(s))
}

object PhoneNumber {
  def fromString(s: String): Option[PhoneNumber] = validate(s).map(PhoneNumber(_))

  def validate(s: String): Option[String] = if (normalize(s).replaceAll("\\d", "").isEmpty) Some(s) else None

  def normalize(s: String): String = s.replaceAll("[\\s-+]", "")
}