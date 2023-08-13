package dev.joss.gatling.sfn.base

import io.gatling.core.session.Session
import org.scalatest.{BeforeAndAfterAll, Suite}

trait EmptySession extends BeforeAndAfterAll { this: Suite =>
  val fakeEventLoop = new FakeEventLoop
  val emptySession: Session = Session("Scenario", 0, fakeEventLoop)
}
