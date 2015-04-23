package com.wiredthing.utils.playSupport

import com.wiredthing.utils.NonBlankString._
import com.wiredthing.utils.slick.IdType
import com.wiredthing.utils.{NonBlankString, PhoneNumber}
import play.api.libs.json._

import scalaz.Scalaz._
import scalaz._

trait UtilsFormats {
  implicit val nonBlankStringFormat = new Format[NonBlankString] {
    override def reads(json: JsValue): JsResult[NonBlankString] = json match {
      case JsString(s) => s.toNbs.map(JsSuccess(_)).getOrElse(JsError(s"Expected non-empty string"))
      case js => JsError(s"Expected JSString but got $js")
    }

    override def writes(nes: NonBlankString): JsValue = JsString(nes.s)
  }

  implicit def nonEmptyListFormat[T: Reads : Writes]: Format[NonEmptyList[T]] = new Format[NonEmptyList[T]] {
    override def reads(json: JsValue): JsResult[NonEmptyList[T]] = {
      Json.fromJson[List[T]](json) match {
        case e: JsError => e
        case JsSuccess(l, p) =>
          l.toNel.map(JsSuccess(_)).getOrElse(JsError("Expected non-empty list"))
      }
    }

    override def writes(o: NonEmptyList[T]): JsValue = Json.toJson(o.list)
  }

  implicit val phoneNumberFormat = new Format[PhoneNumber] {
    override def reads(json: JsValue): JsResult[PhoneNumber] = json match {
      case JsString(s) => PhoneNumber.fromString(s).map(JsSuccess(_)).getOrElse(JsError(s"Could not parse '$s' as a phone number"))
      case _ => JsError(s"Expected String but got $json")
    }

    override def writes(p: PhoneNumber): JsValue = JsString(p.s)
  }

  implicit def idFormat[T] = new Format[IdType[T]] {
    override def reads(json: JsValue): JsResult[IdType[T]] = json.validate[NonBlankString].map(IdType[T](_))

    override def writes(o: IdType[T]): JsValue = JsString(o.id.s)
  }
}
