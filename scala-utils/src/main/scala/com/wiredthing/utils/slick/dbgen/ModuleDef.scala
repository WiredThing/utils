package com.wiredthing.utils.slick.dbgen

case class ModuleDef(name: String, tables: Seq[TableGen], dependsOn: Seq[ModuleDef] = Seq()) {
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
}
