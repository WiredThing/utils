package com.wiredthing.utils

import scalaz._

package object validation {
  type Validated[+T] = ValidationNel[String, T]

  case class ValidationError(errs: NonEmptyList[String]) extends Exception(s"$errs")
}
