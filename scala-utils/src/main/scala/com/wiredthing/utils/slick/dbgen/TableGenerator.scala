package com.wiredthing.utils.slick.dbgen

import com.wiredthing.utils.NonBlankString
import com.wiredthing.utils.slick.IdType
import shapeless._
import shapeless.ops.hlist.ToTraversable
import shapeless.ops.record.{Keys, Values}

trait StringOps {
  def decamelise(s: String) = s.replaceAll("([a-z])([A-Z])", "$1_$2")

  def lowerCaseFirst(s: String): String = s.substring(0, 1).toLowerCase + s.substring(1)

  def stripFromEnd(s: String, count: Int) = s.substring(0, s.length - count)
}

case class TableRow[T](implicit ty: Typeable[T]) extends StringOps {

  val name = ty.describe

  val root = name.substring(0, name.length - 3)

  val tableSQLName = decamelise(root).toUpperCase

  val tableClassName = s"${root}Table"

  val classDef = s"""class $tableClassName(tag: Tag) extends Table[$name](tag, "$tableSQLName")"""
}

trait TableGen {
  def genTable(): Seq[String]
}

object TableGenerator {
  implicit def idTypeTypeable[T](implicit tt: Typeable[T]) = new Typeable[IdType[T]] {
    override def cast(t: Any): Option[IdType[T]] = t match {
      case _: IdType[_] => Some(t.asInstanceOf[IdType[T]])
      case _ => None
    }

    override def describe: String = {
      val rowTypeName = tt.describe
      if (rowTypeName.endsWith("Row")) rowTypeName.substring(0, rowTypeName.length - 3) + "Id"
      else s"IdType[${tt.describe}]"
    }
  }
}



class TableGenerator[T, R <: HList, KO <: HList, K, KLub, VO <: HList](row: TableRow[T])(implicit
                                                                                         lgen: LabelledGeneric.Aux[T, R],
                                                                                         keys: Keys.Aux[R, KO],
                                                                                         values: Values.Aux[R, VO],
                                                                                         fold: FoldTypes[VO],
                                                                                         travK: ToTraversable.Aux[KO, List, KLub]) extends TableGen with StringOps {
  lazy val namesAndTypes: List[TableColumn[_]] = {
    val names = keys().toList.asInstanceOf[List[Symbol]].map(_.name)
    val types = fold()
    names.zip(types).map { case (n, t) => TableColumn(n, t) }
  }


  def generateDefsForColumn(col: TableColumn[_]): Seq[String] = {
    val colOpts = if (col.opts.isEmpty) "" else s""", ${col.opts.mkString(", ")}"""
    val colDef = s"""def ${col.n} = column[${col.ty.describe}]("${col.sqlName}"$colOpts)"""

    if (col.n.endsWith("Id")) {
      val indexRoot = stripFromEnd(col.sqlName, 3)
      val fkSQLName = s"${row.root.toLowerCase}_${indexRoot.toLowerCase}_fk"
      val idxSQLName = s"${row.root.toLowerCase}_${indexRoot.toLowerCase}_idx"
      val idStripped = stripFromEnd(col.n, 2)
      val identifierRoot = lowerCaseFirst(idStripped)
      val fk = s"""def $identifierRoot = foreignKey("$fkSQLName", ${col.n}, ${identifierRoot + "Table"})(_.id, onDelete = ForeignKeyAction.Cascade)"""
      val index = s"""def ${identifierRoot}Index = index("$idxSQLName", ${col.n})"""
      Seq(colDef, fk, index)
    } else Seq(colDef)
  }


  lazy val genTable: Seq[String] = {
    val colDefs = namesAndTypes.flatMap(generateDefsForColumn)


    val typeMappers = namesAndTypes.filter(_.needsTypeMapper).map(_.typeMapper)

    val starDef = s"def * = (${namesAndTypes.map(_.n).mkString(", ")}) <> (${row.name}.tupled, ${row.name}.unapply)"

    Seq(
      Seq(typeMappers: _*),
      Seq(queryAlias, row.classDef + " {"),
      Seq(colDefs.map(d => "    " + d): _*),
      Seq("    " + starDef, "}", tableVal)
    ).flatten
  }


  lazy val tableVal = s"lazy val ${lowerCaseFirst(row.root)}Table = TableQuery[${row.tableClassName}]"

  val queryAlias = s"type ${row.root}Query = Query[${row.root}Table, ${row.name}, Seq]"

}



