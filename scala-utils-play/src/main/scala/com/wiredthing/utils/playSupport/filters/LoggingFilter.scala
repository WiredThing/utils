package com.wiredthing.utils.playSupport.filters

import java.util.Date

import com.wiredthing.utils.playSupport.rest.RequestContext
import org.apache.commons.lang3.time.FastDateFormat
import org.joda.time.{DateTime, DateTimeZone}
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{Filter, RequestHeader, Result}

import scala.concurrent.Future

object LoggingFilter extends Filter {
  private val dateFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss.SSSZZ")

  def apply(next: (RequestHeader) => Future[Result])(rh: RequestHeader): Future[Result] = {
    val startTime = DateTime.now.withZone(DateTimeZone.UTC).getMillis

    val result = next(rh)

    if (needsLogging(rh)) logString(rh, result, startTime, DateTime.now.withZone(DateTimeZone.UTC).getMillis).map(Logger.info)

    result
  }

  def needsLogging(request: RequestHeader): Boolean = true

  //  def needsLogging(request: RequestHeader): Boolean = {
  //    import play.api.Routes
  //    (for {
  //      name <- request.tags.get(Routes.ROUTE_CONTROLLER)
  //    } yield ControllerConfig.paramsForController(name).needsLogging).getOrElse(true)
  //  }

  def logString(rh: RequestHeader, result: Future[Result], startTime: Long, endTime: Long): Future[String] = {
    val rc = RequestContext.fromHeaders(rh.headers)
    val start = dateFormat.format(new Date(startTime))
    val elapsedTime = endTime - startTime

    result.map {
      result => s"${rc.requestChain.value} $start ${rh.method} ${rh.uri} ${result.header.status} ${elapsedTime}ms"
    }.recover {
      case t => s"${rc.requestChain.value} $start ${rh.method} ${rh.uri} $t ${elapsedTime}ms"
    }
  }
}