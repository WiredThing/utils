package com.wiredthing.utils.playSupport.rest

import java.util.UUID

import play.api.mvc.Headers

import scala.util.Random

case class SessionId(id: String)

case class RequestId(id: String)

case class RequestContext(sessionId: SessionId, requestId: RequestId, requestChain: RequestChain) {
  def toHeaders = Seq(
    HeaderNames.xSessionId -> sessionId.id,
    HeaderNames.xRequestId -> requestId.id,
    HeaderNames.xRequestChain -> requestChain.value
  )
}

object RequestContext {
  def fromHeaders(headers: Headers): RequestContext = {
    val sessionId = headers.get(HeaderNames.xSessionId).map(SessionId).getOrElse(generateSessionId)
    val requestId = headers.get(HeaderNames.xRequestId).map(RequestId).getOrElse(generateRequestId)
    val requestChain = headers.get(HeaderNames.xRequestChain) match {
      case Some(rc) => RequestChain(rc).extend
      case None => RequestChain.init
    }

    RequestContext(sessionId, requestId, requestChain)
  }

  def apply(): RequestContext = RequestContext(generateSessionId, generateRequestId, RequestChain.init)

  def generateRequestId: RequestId = RequestId(UUID.randomUUID().toString)

  def generateSessionId: SessionId = SessionId(UUID.randomUUID().toString)
}

case class RequestChain(value: String) extends AnyVal {
  def extend = RequestChain(s"$value-${RequestChain.newComponent}")
}

object RequestChain {
  def format(i: Int) = f"$i%04x"

  def newComponent: String = format(Random.nextInt & 0xffff)

  def init = RequestChain(newComponent)
}