package com.wiredthing.utils.slick.dbgen

import org.scalatest.{Matchers, FlatSpec}
import shapeless.Typeable

class TableColumnTest extends FlatSpec with Matchers {

  "TableColumn[String]" should "report that it is a string type" in {
    TableColumn("col", Typeable[String]).isStringType shouldBe true
  }

  "TableColumn[BigDecimal]" should "give a sql type option" in {
    TableColumn("col", Typeable[BigDecimal]).numOpt shouldBe Some("""O.SqlType("decimal(9, 2)")""")
  }

}
