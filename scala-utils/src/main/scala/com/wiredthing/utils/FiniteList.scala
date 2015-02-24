package com.wiredthing.utils

import shapeless.ops.nat.LTEq
import shapeless.{Nat, Sized}

class FiniteList[T, N <: Nat] {
  ???
}

object FiniteList {
  def apply[T, N <: Nat, M <: Nat](l: Sized[T, N])(implicit ev: LTEq[N, M]): FiniteList[T, M] = ???
}
