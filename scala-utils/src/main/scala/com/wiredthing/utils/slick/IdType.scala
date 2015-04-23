package com.wiredthing.utils.slick

import com.wiredthing.utils.NonBlankString
import com.wiredthing.utils.NonBlankString._

case class IdType[T](id: NonBlankString = java.util.UUID.randomUUID.toString.toNbs.get)