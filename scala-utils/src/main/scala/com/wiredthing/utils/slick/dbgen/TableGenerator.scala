package com.wiredthing.utils.slick.dbgen

import com.wiredthing.utils.slick.IdType
import shapeless._
import shapeless.ops.hlist.ToTraversable
import shapeless.ops.record.{Keys, Values}

case class TableRow[T](implicit ty: Typeable[T]) {
  def decamelise(s: String) = s.replaceAll("([a-z])([A-Z])", "$1_$2")

  val name = ty.describe

  val root = name.substring(0, name.length - 3)

  val tableSQLName = decamelise(root).toUpperCase

  val tableClassName = s"${root}Table"

  val classDef = s"""class $tableClassName(tag: Tag) extends Table[$name](tag, "$tableSQLName")"""
}

trait TableGen {
  def genTable(): Seq[String]
}

class TableGenerator[T, R <: HList, KO <: HList, K, KLub, VO <: HList](row: TableRow[T])(implicit
                                                                                         lgen: LabelledGeneric.Aux[T, R],
                                                                                         keys: Keys.Aux[R, KO],
                                                                                         values: Values.Aux[R, VO],
                                                                                         fold: FoldTypes[VO],
                                                                                         travK: ToTraversable.Aux[KO, List, KLub]) extends TableGen {


  implicit def idTypeTypeable[T](implicit tt: Typeable[T]) = new Typeable[IdType[T]] {
    override def cast(t: Any): Option[IdType[T]] =
      if (t.isInstanceOf[IdType[_]]) Some(t.asInstanceOf[IdType[T]]) else None

    override def describe: String = {
      val rowTypeName = tt.describe
      if (rowTypeName.endsWith("Row")) rowTypeName.substring(0, rowTypeName.length - 3) + "Id"
      else s"IdType[${tt.describe}]"
    }
  }

  lazy val namesAndTypes: List[(String, Typeable[_])] = {
    val names = keys().toList.asInstanceOf[List[Symbol]].map(_.name)
    val types = fold()
    names.zip(types)
  }

  def isStringType(t: String) = t match {
    case "String" | "Option[String]" | "NonBlankString" | "Option[NonBlankString]" => true
    case _ => false
  }

  lazy val genTable: Seq[String] = {
    val colDefs = namesAndTypes.flatMap { case (n, t) =>
      val colName = row.decamelise(n).toLowerCase

      val pkOpt = if (n == "id") ", O.PrimaryKey" else ""

      val lengthOpt =
        if (n == "id" || n.endsWith("Id")) ", O.Length(36)"
        else if (isStringType(t.describe)) ", O.Length(255)"
        else ""

      val col = s"""def $n = column[$t]("$colName"$pkOpt$lengthOpt)"""

      if (n.endsWith("Id")) {
        val indexRoot = colName.substring(0, n.length - 2)
        val fkName = s"${row.root.toLowerCase}_${indexRoot.toLowerCase}_fk"
        val idxName = s"${row.root.toLowerCase}_${indexRoot.toLowerCase}_idx"
        val fk = s"""def $indexRoot = foreignKey("$fkName", $n, ${indexRoot}Table)(_.id, onDelete = ForeignKeyAction.Cascade)"""
        val index = s"""def ${indexRoot}Index = index("$idxName", $n)"""
        Seq(col, fk, index)
      } else Seq(col)
    }

    val knownTypes = Seq("String", "Long", "Boolean", "Int", "Short", "NonBlankString", "PhoneNumber")

    def isOptionOfKnownType(t: String): Boolean = t.startsWith("Option[") && !needsTypeMapper(t.substring(7, t.length - 1))

    def isIdType(t: String): Boolean = t.endsWith("Id") || t.startsWith("IdType[")

    def needsTypeMapper(t: String): Boolean = !(knownTypes.contains(t) || isOptionOfKnownType(t) || isIdType(t))

    /*
    * Relies on a conversion that the member name of a wrapper type is the same as the last
    * part of the type name. E.g. SenderKey(key:String) or SMSProviderName(name:String)
    * TODO: Only create type mappers for classes with a single member
    * TODO: Use type information to extract the name of the member
     */
    def typeMapper(ty: Typeable[_]) = {
      val t = ty.describe.replace("Option[", "").replace("]", "")
      val s = row.decamelise(t).toLowerCase
      val memberNameIndex = s.lastIndexOf("_")
      val memberName = s.substring(memberNameIndex + 1)
      s"implicit def ${t}Mapper: BaseColumnType[$t] = MappedColumnType.base[$t, String](_.$memberName, $t)"
    }

    val typeMappers = namesAndTypes.map(_._2).filter(ty => needsTypeMapper(ty.describe)).map(typeMapper)

    val starDef = s"def * = (${namesAndTypes.map(_._1).mkString(", ")}) <> (${row.name}.tupled, ${row.name}.unapply)"

    Seq(
      Seq(typeMappers: _*),
      Seq(queryAlias(row), row.classDef + " {"),
      Seq(colDefs.map(d => "    " + d): _*),
      Seq("    " + starDef, "}", tableVal(row))
    ).flatten
  }

  def tableVal[T](decl: TableRow[T]) = s"lazy val ${decl.root.toLowerCase}Table = TableQuery[${decl.tableClassName}]"

  def queryAlias[T](decl: TableRow[T]) = {
    //type NotificationQuery = Query[NotificationTable, NotificationRow, Seq]
    val root = decl.root
    s"type ${root}Query = Query[${root}Table, ${decl.name}, Seq]"
  }
}



