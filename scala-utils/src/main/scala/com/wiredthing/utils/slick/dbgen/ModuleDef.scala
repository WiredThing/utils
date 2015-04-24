package com.wiredthing.utils.slick.dbgen

import shapeless.ops.hlist.ToTraversable
import shapeless.ops.record.{Values, Keys}
import shapeless.{LabelledGeneric, HList}

case class ModuleDef(name: String, tables: Seq[TableGen] = Seq(), dependsOn: Seq[ModuleDef] = Seq()) {
  def generate: Seq[String] = {
    val selfTypes: Seq[String] = "DBBinding" +: "MappedTypes" +: dependsOn.map(_.name)
    val head = Seq(
      s"trait $name {",
      s"  self: ${selfTypes.mkString(" with ")} =>",
      s"  import driver.api._"
    )

    val tableDefs = tables.flatMap("" +: _.genTable().map("  " + _))

    val foot = Seq("}")

    (head :: tableDefs :: foot :: Nil).flatten
  }

  def withTableFor[T, R <: HList, KO <: HList, K, KLub, VO <: HList]
  (row: TableRow[T])
  (implicit
   lgen: LabelledGeneric.Aux[T, R],
   keys: Keys.Aux[R, KO],
   values: Values.Aux[R, VO],
   fold: FoldTypes[VO],
   travK: ToTraversable.Aux[KO, List, KLub]) = copy(tables = (new TableGenerator(row) +: tables).reverse)

  def dependsOn(mods: ModuleDef*): ModuleDef = copy(dependsOn = (mods ++: dependsOn).reverse)
}
