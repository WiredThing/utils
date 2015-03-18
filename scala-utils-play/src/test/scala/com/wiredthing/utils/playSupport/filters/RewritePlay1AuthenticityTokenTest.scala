package com.wiredthing.utils.playSupport.filters

import org.scalatest._
import play.api.test.Helpers._
import play.api.test.{FakeRequest, WithApplication}

class RewritePlay1AuthenticityTokenTest extends FlatSpec with Matchers {
  "RewritePlay1AuthenticityToken.addSessionProperty" should "add the property to an empty session" in new WithApplication {
    val sut = new RewritePlay1AuthenticityToken()
    sut.addSessionProperty(FakeRequest(POST, "/"), ("foo" -> "bar")).session.get("foo") shouldBe Some("bar")
  }

  it should "override the session if it exists" in new WithApplication {
    val sut = new RewritePlay1AuthenticityToken()
    val request = FakeRequest(POST, "/").withSession("foo" -> "baz")

    sut.addSessionProperty(request, ("foo" -> "bar")).session.get("foo") shouldBe Some("bar")
  }

  it should "not touch any other session properties" in new WithApplication {
    val sut = new RewritePlay1AuthenticityToken()

    val request = FakeRequest(POST, "/").withSession("fib" -> "baz")

    sut.addSessionProperty(request, ("foo" -> "bar")).session.get("fib") shouldBe Some("baz")
  }
}
