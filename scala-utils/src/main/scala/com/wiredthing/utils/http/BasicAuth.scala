package com.wiredthing.utils.http

import com.wiredthing.utils.NonBlankString
import com.wiredthing.utils.base64.Base64._

case class BasicAuth(username: NonBlankString, password: Option[NonBlankString]) {
  def encodeBase64: String = {
    val s = password match {
      case Some(pwd) => s"$username:$pwd"
      case None => s"$username:"
    }

    s.getBytes.toBase64
  }

  def headerValue: String = s"Basic $encodeBase64"
}
