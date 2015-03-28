package com.wiredthing.utils.base64

import org.scalatest.{FlatSpec, Matchers}
import Base64._

import scala.util.Failure

class Base64$Test extends FlatSpec with Matchers {
  "toByteArray" should "return a Failure for a badly-formatted string" in {
    "bad format".toByteArray shouldBe a[Failure[_]]
  }
}
