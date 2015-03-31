package com.wiredthing.utils.playSupport

import play.api.data.validation.ValidationError
import play.api.libs.json._
import play.api.libs.ws.WSResponse

import scala.concurrent.{ExecutionContext, Future}

package object rest {

  case class BadGatewayResponse(m: String, jsErrs: Option[Seq[(JsPath, Seq[ValidationError])]] = None) extends Exception(m)

  case class GatewayTimeoutResponse(m: String) extends Exception(m)

  case class InternalServerErrorResponse(m: String) extends Exception(m)

  implicit val unitReads = new Reads[Unit]{
    override def reads(json: JsValue): JsResult[Unit] = JsSuccess(Unit)
  }


  def processResponse[A: Reads](url: String, response: Future[WSResponse])(implicit ec: ExecutionContext): Future[A] = response.map { r =>
    r.status match {
      case 200 => Json.fromJson[A](r.json) match {
        case JsSuccess(a, _) => a
        case JsError(errs) => throw new BadGatewayResponse(s"Could not decode json from '$url'", Some(errs))
      }
      case 404 => throw new InternalServerErrorResponse(s"received 404 for url '$url'")
      case 500 => throw new BadGatewayResponse(s"received 500 for url '$url' with body '${r.body}'")
      case s => throw new InternalServerErrorResponse(s"received $s} for url '$url' with body '${r.body}'")
    }
  }

  val contentTypeJson = "Content-Type" -> "application/json"
}
