package com.wiredthing.utils.slick.dbgen

import shapeless._

object FoldTypes {
  implicit def hnilStrings: FoldTypes[HNil] =
    new FoldTypes[HNil] {
      def apply() = List()
    }

  implicit def hconsStrings[H, T <: HList](implicit th: Typeable[H], ft: FoldTypes[T]): FoldTypes[H :: T] =
    new FoldTypes[H :: T] {
      def apply() = th :: ft()
    }
}

trait FoldTypes[L <: HList] {
  def apply(): List[Typeable[_]]
}