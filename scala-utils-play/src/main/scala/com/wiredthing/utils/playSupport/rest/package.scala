package com.wiredthing.utils.playSupport

import java.net.{ConnectException, SocketTimeoutException}

import play.api.data.validation.ValidationError
import play.api.libs.json._
import play.api.libs.ws.WSResponse

import scala.concurrent.{TimeoutException, ExecutionContext, Future}

package object rest {

  type JsErrorsType = Seq[(JsPath, Seq[ValidationError])]

  implicit val unitReads = new Reads[Unit] {
    override def reads(json: JsValue): JsResult[Unit] = JsSuccess(Unit)
  }

  protected def upstreamResponseMessage(verbName: String, url: String, status: Int, responseBody: String): String = {
    s"$verbName of '$url' returned $status. Response body: '$responseBody'"
  }

  protected def badRequestMessage(verbName: String, url: String, responseBody: String): String = {
    s"$verbName of '$url' returned 400 (Bad Request). Response body '$responseBody'"
  }

  protected def notFoundMessage(verbName: String, url: String, responseBody: String): String = {
    s"$verbName of '$url' returned 404 (Not Found). Response body: '$responseBody'"
  }

  protected def badGatewayMessage(verbName: String, url: String, e: Exception): String = {
    s"$verbName of '$url' failed. Caused by: '${e.getMessage}'"
  }

  protected def gatewayTimeoutMessage(verbName: String, url: String, e: Exception): String = {
    s"$verbName of '$url' timed out with message '${e.getMessage}'"
  }


  def processResponse[A: Reads : Manifest](method: String, url: String, response: Future[WSResponse])(implicit ec: ExecutionContext): Future[A] = response.map { r =>
    r.status match {
      case 200 => Json.fromJson[A](r.json) match {
        case JsSuccess(a, _) => a
        case JsError(errs) => throw new JsValidationException(method, url, r.body, implicitly[Manifest[A]].getClass, errs)
      }
      case 400 => throw new BadRequestException(badRequestMessage(method, url, r.body))
      case 404 => throw new NotFoundException(notFoundMessage(method, url, r.body))

      case code if code >= 400 && code < 500 => throw new Upstream4xxResponse(upstreamResponseMessage(method, url, code, r.body), code, 500)
      case code if code >= 500 && code < 600 => throw new Upstream5xxResponse(upstreamResponseMessage(method, url, code, r.body), code, 502)
      case status => throw new Exception(s"$method to $url failed with status $status. Response body: '${r.body}'")
    }
  }.recover {
    case e: TimeoutException => throw new GatewayTimeout(gatewayTimeoutMessage(method, url, e))
    case e: ConnectException => throw new BadGatewayException(badGatewayMessage(method, url, e))
  }

  val contentTypeJson = "Content-Type" -> "application/json"
}
