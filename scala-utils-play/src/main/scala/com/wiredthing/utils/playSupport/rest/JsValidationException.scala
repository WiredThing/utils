package com.wiredthing.utils.playSupport.rest

import play.api.libs.json.JsPath
import play.api.data.validation.ValidationError
import scala.language.existentials

case class JsValidationException(method: String,
                                 url: String,
                                 invalidJson: String,
                                 readingAs: Class[_],
                                 errors: Seq[(JsPath, Seq[ValidationError])]) extends Exception {
  override def getMessage: String = {
    s"$method of '$url' returned body '$invalidJson'. Attempting to convert to ${readingAs.getName} gave errors: $errors"
  }
}