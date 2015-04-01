package com.wiredthing.utils.playSupport.rest

import play.api.http.Status._

class HttpException(val message: String, val responseCode: Int) extends Exception(message)

class NotFoundException(message: String) extends HttpException(message, NOT_FOUND)

class BadGatewayException(message: String) extends HttpException(message, BAD_GATEWAY)

class BadRequestException(message: String) extends HttpException(message, BAD_REQUEST)

class UnauthorizedException(message: String) extends HttpException(message, UNAUTHORIZED)

class GatewayTimeout(message: String) extends HttpException(message, GATEWAY_TIMEOUT)

class RequestEntityTooLargeException(message: String) extends HttpException(message, REQUEST_ENTITY_TOO_LARGE)

class PreconditionFailedException(message: String) extends HttpException(message, PRECONDITION_FAILED)

case class Upstream4xxResponse(message: String, upstreamResponseCode: Int, reportAs: Int) extends Exception(message)

case class Upstream5xxResponse(message: String, upstreamResponseCode: Int, reportAs: Int) extends Exception(message)

