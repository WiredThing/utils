package controllers.status

import scala.concurrent.Future

import play.api.libs.json.Json
import play.api.libs.ws.WS
import play.api.mvc._
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import system.status.PlayConfiguration

object StatusController extends Controller{
  def ping = Action { request =>
    Ok("OK")
  }

  def checkAll = Action.async {
    val fs = PlayConfiguration.components.map { component =>
      WS.url(s"http://${component.host}:${component.port}/status/check_all").get().map { r =>
        r.status match {
          case 200 => ComponentStatus(component.name, "ok", None)
          case s => ComponentStatus(component.name, "failed", Some(s.toString))
        }
      }.recover {
        case t => ComponentStatus(component.name, "failed", Some(t.getMessage))
      }
    }

    Future.sequence(fs).map { ss =>
      ss.find(_.status == "failed") match {
        case None => Ok(Json.toJson(ss))
        case _ => ServiceUnavailable(Json.toJson(ss))
      }
    }
  }
}
