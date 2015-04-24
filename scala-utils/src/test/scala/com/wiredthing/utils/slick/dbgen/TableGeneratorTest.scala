package com.wiredthing.utils.slick.dbgen

import com.wiredthing.utils.slick.IdType
import org.scalatest.{FlatSpec, Matchers}
import shapeless.Typeable


object TableGeneratorTestData {
  type TestId = IdType[TestRow]
  type AnotherTestId = IdType[AnotherTestRow]

  case class TestRow(column1: String, id: TestId, anotherTestId: AnotherTestId)

  case class AnotherTestRow(counter: Int, id: AnotherTestId)

  case class BigDecimalTestRow(n: BigDecimal, on: Option[BigDecimal])

}

class TableGeneratorTest extends FlatSpec with Matchers {

  import TableGeneratorTestData._


  implicit def idTypeTypeable[T](implicit tt: Typeable[T]) = TableGenerator.idTypeTypeable

  "generateColumnDefs" should "give a column, fk and index" in {
    val gen = new TableGenerator(TableRow[TestRow])

    val expectedColDef = """def anotherTestId = column[AnotherTestId]("another_test_id", O.Length(IdType.length))"""
    val expectedFkDef = """def anotherTest = foreignKey("test_another_test_fk", anotherTestId, anotherTestTable)(_.id, onDelete = ForeignKeyAction.Cascade)"""
    val expectedIdxDef = """def anotherTestIndex = index("test_another_test_idx", anotherTestId)"""

    val lines = gen.generateDefsForColumn(TableColumn("anotherTestId", Typeable[AnotherTestId]))
    lines(0) shouldBe expectedColDef
    lines(1) shouldBe expectedFkDef
    lines(2) shouldBe expectedIdxDef
  }

  "tableVal" should "give the right TableQuery definition" in {
    val gen = new TableGenerator(TableRow[AnotherTestRow])

    gen.tableVal shouldBe "lazy val anotherTestTable = TableQuery[AnotherTestTable]"
  }

  "generatColumnDefs" should "give right options for BigDecimal" in {
    val gen = new TableGenerator(TableRow[BigDecimalTestRow])

    gen.generateDefsForColumn(TableColumn("n", Typeable[BigDecimal])).head shouldBe """def n = column[BigDecimal]("n", O.SqlType("decimal(9, 2)"))"""

    gen.generateDefsForColumn(TableColumn("on", Typeable[Option[BigDecimal]])).head shouldBe """def on = column[Option[BigDecimal]]("on", O.SqlType("decimal(9, 2)"))"""

  }
}
