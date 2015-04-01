package com.wiredthing.utils.playSupport.actions

import com.wiredthing.utils.playSupport.rest._
import play.api.mvc._

import scala.concurrent.Future

class SessionRequest[A](val context: RequestContext, request: Request[A]) extends WrappedRequest[A](request)

object SessionAction
  extends ActionBuilder[SessionRequest]
  with ActionRefiner[Request, SessionRequest] {

  override protected def refine[A](input: Request[A]): Future[Either[Result, SessionRequest[A]]] = Future.successful {
    Right(new SessionRequest(RequestContext.fromHeaders(input.headers), input))
  }
}

