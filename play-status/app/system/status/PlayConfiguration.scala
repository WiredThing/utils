package system.status

import com.typesafe.config.Config
import net.ceedubs.ficus.Ficus._
import play.api.Play
import play.api.Play.current

case class Component(name: String, host: String, port: Int)

object PlayConfiguration {
  lazy val config: Config = Play.configuration.underlying

  import net.ceedubs.ficus.readers.ArbitraryTypeReader._

  lazy val components: List[Component] = config.as[List[Component]]("components")


}
