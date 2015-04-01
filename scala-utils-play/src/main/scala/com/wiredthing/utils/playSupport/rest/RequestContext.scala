package com.wiredthing.utils.playSupport.rest

import java.util.UUID

import play.api.mvc.{Headers, RequestHeader}

import scala.util.Random

case class SessionId(id: String)

case class RequestId(id: String)

case class RequestContext(sessionId: SessionId, requestId: RequestId, requestChain: RequestChain)

object RequestContext {
  def fromHeaders(headers: Headers): RequestContext = {
    val sessionId = SessionId(headers.get(HeaderNames.xSessionId).getOrElse(generateSessionId))
    val requestId = RequestId(headers.get(HeaderNames.xRequestId).getOrElse(generateSessionId))
    val requestChain = headers.get(HeaderNames.xRequestChain) match {
      case Some(rc) => RequestChain(rc).extend
      case None => RequestChain.init
    }

    RequestContext(sessionId, requestId, requestChain)
  }

  def generateRequestId = UUID.randomUUID().toString

  def generateSessionId = UUID.randomUUID().toString
}

case class RequestChain(value: String) extends AnyVal {
  def extend = RequestChain(s"$value-${RequestChain.newComponent}")
}

object RequestChain {
  def newComponent = (Random.nextInt & 0xffff).toHexString

  def init = RequestChain(newComponent)
}