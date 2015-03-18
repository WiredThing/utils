package com.wiredthing.utils.playSupport.filters

trait SessionConfig {
  // The session timestamp will be stored in the session as a property with this name
  def sessionTimestampName: String

  // The maximum time, in milliseconds, that the session can remain valid without having been refreshed
  def maxAge: Long

  // Used as the root of the path when redirecting to the login page.
  def untrustedRouteRoot: String

  // Paths starting with these roots are accessible without a valid session, e.g. the login page or
  // assets
  def untrustedRoots: Seq[String]

  // Paths starting with these roots are considered API calls and will give an Unauthorized
  // response rather than being redirected to the login page when accessed without a valid session
  def apiRoots: Seq[String]

  def logRequests: Boolean
}

object DefaultSessionConfig extends SessionConfig {
  val sessionTimestampName: String = "___TS"
  val maxAge: Long = 30 * 60 * 1000
  val untrustedRouteRoot: String = "/untrusted"
  val untrustedRoots: Seq[String] = Seq(untrustedRouteRoot, "/assets")
  val apiRoots: Seq[String] = Seq("/api")
  val logRequests: Boolean = false
}