package com.wiredthing.utils.slick.dbgen

import org.scalatest.{Matchers, FlatSpec}



class TableRowTest extends FlatSpec with Matchers {
  case class TestRow(s:String)

  "name" should "be name of case class type" in {
    val tr = TableRow[TestRow]

    tr.name shouldBe "TestRow"
  }

  "root" should "be name of case class type with 'Row' removed" in {
    val tr = TableRow[TestRow]

    tr.root shouldBe "Test"
  }

}
