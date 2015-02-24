package system.status

import com.typesafe.config.Config
import net.ceedubs.ficus.Ficus._
import play.api.Play
import play.api.Play.current

case class ComponentDetail(host: String, port: Int)

case class Component(name: String, host: String, port: Int)

object PlayConfiguration {
  lazy val config: Config = Play.configuration.underlying

  import net.ceedubs.ficus.readers.ArbitraryTypeReader._

  lazy val components: List[Component] = config.as[Option[Map[String, ComponentDetail]]]("components") match {
    case Some(cs) => cs.map { e =>
      val (name, cd) = e
      Component(name, cd.host, cd.port)
    }.toList
    case None => List()
  }

}
