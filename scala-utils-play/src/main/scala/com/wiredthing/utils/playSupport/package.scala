package com.wiredthing.utils

import play.api.mvc._
import play.mvc.Http.HeaderNames

package object playSupport {
  implicit class RequestHeaderWrapper(val rh: RequestHeader) extends AnyVal {
    def withHeaders(headers: (String, Seq[String])*): RequestHeader =
      rh.copy(headers = new Headers {
        override protected val data: Seq[(String, Seq[String])] = (rh.headers.toMap ++ headers).toSeq
      })

    def withCookie(cookie: Cookie): RequestHeader = {
      val existingCookies = rh.headers.get(play.api.http.HeaderNames.COOKIE).map(Cookies.decode).getOrElse(Seq())
      val newCookies = cookie +: existingCookies.filterNot(_.name == cookie.name)

      rh.withHeaders(HeaderNames.COOKIE -> Seq(Cookies.encode(newCookies)))
    }

    def withSessionProperty(p: (String, String)): RequestHeader = withCookie(Session.encodeAsCookie(rh.session + p))
  }
}
