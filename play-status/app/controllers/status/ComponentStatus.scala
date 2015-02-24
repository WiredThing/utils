
package controllers.status

import play.api.libs.json.Json

case class ComponentStatus(name: String, status: String = "ok", reason: Option[String] = None)

object ComponentStatus {
  implicit val write = Json.writes[ComponentStatus]
}
