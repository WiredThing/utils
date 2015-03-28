package com.wiredthing.utils

import org.scalatest._

class PhoneNumber$Test extends FlatSpec with Matchers {

  "PhoneNumber.fromString" should "create PhoneNumber from '447515352021'" in {
    PhoneNumber.fromString("447515352021") shouldBe Some(PhoneNumber("447515352021"))
  }

  "normalize" should "convert '+44 7515 352021' to '447515352021'" in {
    PhoneNumber.normalize("+44 7515 352021") shouldBe "447515352021"
  }



}
