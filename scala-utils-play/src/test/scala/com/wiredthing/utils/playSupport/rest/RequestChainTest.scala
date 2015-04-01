package com.wiredthing.utils.playSupport.rest

import org.scalatest.{FlatSpec, Matchers}

class RequestChainTest extends FlatSpec with Matchers {
  "format" should "format 0 as '0000'" in {
    RequestChain.format(0) shouldBe "0000"
  }

  it should "format 0xce as '00ce'" in {
    RequestChain.format(0xce) shouldBe "00ce"
  }

  it should "format 0xffee as 'ffee'" in {
    RequestChain.format(0xffee) shouldBe "ffee"
  }
}
