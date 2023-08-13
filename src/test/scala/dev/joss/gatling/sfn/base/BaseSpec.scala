package dev.joss.gatling.sfn.base

import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

trait BaseSpec
    extends AnyFlatSpecLike
    with Matchers
    with MockitoSugar
    with ScalaCheckDrivenPropertyChecks
