package com.wiredthing.utils.http

import org.scalatest.{FlatSpec, Matchers}
import com.wiredthing.utils.NonBlankString._

class BasicAuthTest extends FlatSpec with Matchers {
  val testBase64 = "QWxhZGRpbjpvcGVuIHNlc2FtZQ=="
  val testHeaderValue = "Basic " + testBase64
  val testUsername = "Aladdin"
  val testPassword = "open sesame"

  "encodeBase64" should "give the expected string" in {
    BasicAuth(testUsername.toNbs.get, Some(testPassword.toNbs.get)).encodeBase64 shouldBe testBase64
  }

  "headerValue" should "give the expected string" in {
    BasicAuth(testUsername.toNbs.get, Some(testPassword.toNbs.get)).headerValue shouldBe s"Basic $testBase64"
  }
}
