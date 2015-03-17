package com.wiredthing.utils.validation

import scalaz.Scalaz._
import scalaz._

trait Validates[T] {
  def validate(a: T): Validated[T]
}

case class NoValidation[T]() extends Validates[T] {
  override def validate(a: T): Validated[T] = Success(a)
}


/**
 * Some handy implementations of the Validates type-class to validate things like
 * lists and options.
 */
trait DefaultValidates {
  implicit def validatesList[T: Validates]: Validates[List[T]] = new Validates[List[T]] {
    override def validate(items: List[T]): Validated[List[T]] = {
      items.zipWithIndex.map { case (item, i) =>
        implicitly[Validates[T]].validate(item) match {
          case Failure(nel) => Failure(nel.map(e => s"$i: $e"))
          case s@Success(_) => s
        }
      }.sequenceU
    }
  }

  implicit def validateNel[T: Validates] = new Validates[NonEmptyList[T]] {
    override def validate(nel: NonEmptyList[T]): Validated[NonEmptyList[T]] = validating(nel.list).map(_.toNel.get)
  }

  implicit def validatesOption[T: Validates] = new Validates[Option[T]] {
    override def validate(oa: Option[T]): Validated[Option[T]] = oa match {
      case Some(a) => validating(a).map(Some(_))
      case None => Success(None)
    }
  }

  def validating[A: Validates](a: A): Validated[A] = implicitly[Validates[A]].validate(a)

}

object validations extends DefaultValidates