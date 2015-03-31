package com.wiredthing.utils.playSupport.actions

import com.wiredthing.utils.NonBlankString
import com.wiredthing.utils.NonBlankString._
import com.wiredthing.utils.http.BasicAuth
import org.scalatest.{FlatSpec, Matchers}

import scalaz.{-\/, \/-}

class BasicAuthExtractionTest extends FlatSpec with Matchers {
  val sut = new BasicAuthExtraction {}

  val testBase64 = "QWxhZGRpbjpvcGVuIHNlc2FtZQ=="
  val malformedHeaderValue = "load of rubbish"
  val oauthAuthHeaderValue = "OAuth " + testBase64
  val testHeaderValue = "Basic " + testBase64
  val testUsername = "Aladdin"
  val testPassword = "open sesame"

  "extractBase64Auth" should "extract the base64-encoded string from the header value" in {
    sut.extractBase64Auth(testHeaderValue) shouldBe \/-(testBase64)
  }

  it should "return an error for malformed header" in {
    sut.extractBase64Auth(malformedHeaderValue) shouldBe a[-\/[_]]
  }

  it should "return an error for an oauth header" in {
    sut.extractBase64Auth(oauthAuthHeaderValue) shouldBe a[-\/[_]]
  }

  "decodeBase64" should "successfully decode a good value" in {
    sut.decodeBase64(testBase64) shouldBe \/-(s"$testUsername:$testPassword")
  }

  it should "return an error for bad base64 data" in {
    sut.decodeBase64("bad base64") shouldBe a[-\/[_]]
  }

  "extractBasicAuth" should "return a BasicAuth if username and password are present" in {
    sut.extractBasicAuth(s"$testUsername:$testPassword") shouldBe \/-(BasicAuth(NonBlankString(testUsername), Some(NonBlankString(testPassword))))
  }

  it should "return a BasicAuth if username is present and password is blank" in {
    sut.extractBasicAuth(s"$testUsername:") shouldBe \/-(BasicAuth(NonBlankString(testUsername), None))
  }

  it should "return an error if username is not present but password is" in {
    sut.extractBase64Auth(s":$testPassword") shouldBe a[-\/[_]]
  }

  it should "return an error if string is blank" in {
    sut.extractBase64Auth("") shouldBe a[-\/[_]]
  }

  "decodeBasicAuth" should "return a BasicAuth for a valid auth header value" in {
    sut.decodeBasicAuth(testHeaderValue) shouldBe \/-(BasicAuth(NonBlankString(testUsername), Some(NonBlankString(testPassword))))
  }
}


