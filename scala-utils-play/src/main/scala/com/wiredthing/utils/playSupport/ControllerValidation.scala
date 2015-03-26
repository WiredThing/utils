package com.wiredthing.utils.playSupport

import com.wiredthing.utils.validation.{DefaultValidates, Validates}
import play.api.libs.json._
import play.api.mvc.{Controller, Result}

import scala.concurrent.{ExecutionContext, Future}
import scalaz.Scalaz._
import scalaz._

/**
 * Add this to a controller to make it easy to run validation on models.
 *
 * First, the json body will be parsed into an object of type A. If that fails, a BadRequest
 * will be generated listing the json errors.
 *
 * If the json conversion succeeds, the object of type A will have its validation run on it
 * (using the Validates type class). If the validation succeeds then the body function will be
 * run on the validated value and the output from that function will be sent in an Ok response
 * as Json.
 *
 * If validation fails a BadRequest will be generated containing a list of the errors found.
 */
trait ControllerValidation extends DefaultValidates {
  self: Controller =>
  def withValidation[A: Validates : Reads, B: Writes](json: JsValue)(body: A => Future[B])(implicit ec: ExecutionContext): Future[Result] = {
    json.validate[A] match {
      case JsSuccess(a, _) => withValidation(a)(body).map(resultFor(_))

      case JsError(errors) => Future {
        val errStrings = errors.map { case (path, errs) => errs.map(ve => s"$path: $ve") }
        BadRequest(Json.toJson(errStrings))
      }
    }
  }

  def resultFor[T: Writes](v: ValidationNel[String, T]): Result = v match {
    case Success(b) => Ok(Json.toJson(b))
    case Failure(errs) => BadRequest(Json.toJson(errs.toList))
  }

  def withValidation[A: Validates, B: Writes](a: A)(body: A => Future[B])(implicit ec: ExecutionContext): Future[ValidationNel[String, B]] = {
    implicitly[Validates[A]].validate(a) match {
      case v@Success(a) => body(a).map(Success(_))
      case Failure(errs) => Future(Failure(errs))
    }
  }
}
