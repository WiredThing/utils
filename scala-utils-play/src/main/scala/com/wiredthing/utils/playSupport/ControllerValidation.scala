package com.wiredthing.utils.playSupport

import com.wiredthing.utils.validation.{DefaultValidates, Validates}
import play.api.libs.json._
import play.api.mvc.{Controller, Result}

import scala.concurrent.{ExecutionContext, Future}
import scalaz.Scalaz._
import scalaz._

trait ControllerValidation extends DefaultValidates {
  self: Controller =>
  def withValidation[A: Validates : Reads, B: Writes](json: JsValue)(body: A => Future[B])(implicit ec: ExecutionContext): Future[Result] = {
    json.validate[A] match {
      case JsSuccess(r, _) => implicitly[Validates[A]].validate(r) match {
        case v@Success(a) => body(a).map(r => Ok(Json.toJson(r)))
        case Failure(errs) => Future(BadRequest(Json.toJson(errs.toList)))
      }
      case JsError(errors) => Future {
        val errStrings = errors.map { case (path, errs) => errs.map(ve => s"$path: $ve")}
        BadRequest(Json.toJson(errStrings))
      }
    }
  }
}
