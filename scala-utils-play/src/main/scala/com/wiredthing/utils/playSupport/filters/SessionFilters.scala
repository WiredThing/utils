package com.wiredthing.utils.playSupport.filters

import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.Results._
import play.api.mvc.{Filter, RequestHeader, Result}

import scala.concurrent.Future
import scala.util.Try


/**
 * This Filter checks the Result for a non-empty Session. If it finds one it
 * updates the `___TS` property with a timestamp based on the session timeout,
 * keeping it fresh.
 */
class SessionRefreshFilter(config: SessionConfig) extends Filter {
  override def apply(next: (RequestHeader) => Future[Result])(rh: RequestHeader): Future[Result] = {
    implicit val irh = rh
    next(rh).map { result =>
      if (result.session.isEmpty) result
      else {
        val ts: Long = System.currentTimeMillis() + config.maxAge
        result.withSession(result.session + (config.sessionTimestampName -> ts.toString))
      }
    }
  }
}

class SessionLoggerFilter(config: SessionConfig) extends Filter {
  override def apply(f: (RequestHeader) => Future[Result])(rh: RequestHeader): Future[Result] = {
    if (config.logRequests) Logger.debug(s"hitting url ${rh.uri} with session  ${rh.session}")
    f(rh)
  }
}

/**
 * Check that the session is valid, unless the incoming path is listed as an
 * untrusted endpoint. The session must have a timestamp in the future and
 * a "username" property. If the session is not valid then the request is
 * redirected to the login page.
 */
class SessionCheckFilter(config: SessionConfig) extends Filter {

  object MatchLong {
    def unapply(s: String): Option[Long] = Try(s.toLong).toOption
  }

  private def needsCheck(path: String): Boolean = !config.untrustedRoots.exists(root => path.startsWith(root))

  private def isApiPath(path: String): Boolean = config.apiRoots.exists(root => path.startsWith(root))

  private def resultForUnauthorizedPath(rh: RequestHeader, flashMessage: Option[(String, String)] = None): Result = {
    // Play will put a tag named ROUTE_CONTROLLER on the RequestHeader if the request was successfully routed.
    // Use this as an indicator that we should bounce to the login page rather than returning a 404. Non-routed
    // requests will just get a 404 NotFound
    rh.tags.get("ROUTE_CONTROLLER").map { x =>
      val result = rh.path match {
        case p if isApiPath(p) => Unauthorized.withNewSession
        case _ => Redirect(s"${config.untrustedRouteRoot}/login").withNewSession
      }
      flashMessage.map(m => result.flashing(m)).getOrElse(result)
    }.getOrElse(NotFound)
  }

  override def apply(next: (RequestHeader) => Future[Result])(rh: RequestHeader): Future[Result] = {
    if (!needsCheck(rh.path)) next(rh)
    else {
      val tsString: String = rh.session.get(config.sessionTimestampName).getOrElse("0")
      (rh.session.get("username"), tsString) match {
        case (Some(_), MatchLong(ts)) if ts >= System.currentTimeMillis() => next(rh)
        case (Some(_), _) => Future(resultForUnauthorizedPath(rh, Some("error" -> s"Your session has expired")))
        case (_, _) => Future(resultForUnauthorizedPath(rh))
      }
    }
  }
}