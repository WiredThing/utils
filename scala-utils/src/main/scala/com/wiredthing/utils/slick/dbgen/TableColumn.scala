package com.wiredthing.utils.slick.dbgen

import shapeless.Typeable

case class TableColumn[T](n: String, ty: Typeable[T]) extends StringOps {
  val tyName = ty.describe

  val isStringType = tyName match {
    case "String" | "Option[String]" | "NonBlankString" | "Option[NonBlankString]" => true
    case _ => false
  }

  val lengthOpt = n match {
    case _ if n == "id" || n.endsWith("Id") => Some("O.Length(IdType.length)")
    case _ if isStringType => Some("O.Length(255)")
    case _ => None
  }

  val pkOpt = if (n == "id") Some("O.PrimaryKey") else None

  val numOpt = tyName match {
    case "BigDecimal" | "Option[BigDecimal]" => Some( """O.SqlType("decimal(9, 2)")""")
    case _ => None
  }

  val knownTypes = Seq("BigDecimal", "String", "Long", "Boolean", "Int", "Short", "NonBlankString", "PhoneNumber")

  val isOptionOfKnownType: Boolean = tyName.startsWith("Option[") && !needsTypeMapper(tyName.substring(7, tyName.length - 1))

  def isIdType(t: String): Boolean = t.endsWith("Id") || t.startsWith("IdType[")

  def needsTypeMapper(t: String): Boolean = !(knownTypes.contains(t) || isOptionOfKnownType || isIdType(t))

  val needsTypeMapper: Boolean = needsTypeMapper(tyName)

  /*
   * Relies on a conversion that the member name of a wrapper type is the same as the last
   * part of the type name. E.g. SenderKey(key:String) or SMSProviderName(name:String)
   * TODO: Only create type mappers for classes with a single member
   * TODO: Use type information to extract the name of the member
    */
  val typeMapper = {
    val t = tyName.replace("Option[", "").replace("]", "")
    val s = decamelise(t).toLowerCase
    val memberNameIndex = s.lastIndexOf("_")
    val memberName = s.substring(memberNameIndex + 1)
    s"implicit def ${t}Mapper: BaseColumnType[$t] = MappedColumnType.base[$t, String](_.$memberName, $t)"
  }

  val opts = Seq(lengthOpt, pkOpt, numOpt).flatten

  val sqlName = decamelise(n).toLowerCase
}
