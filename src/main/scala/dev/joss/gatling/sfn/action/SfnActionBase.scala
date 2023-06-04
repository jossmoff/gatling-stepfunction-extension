package dev.joss.gatling.sfn.action

import dev.joss.gatling.sfn.protocol.SfnProtocol
import io.gatling.commons.stats.{KO, OK}
import io.gatling.core.action.ExitableAction
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.session.Session
import io.gatling.core.structure.ScenarioContext

import java.util.concurrent.Callable
import scala.util.Try

trait SfnActionBase extends ExitableAction {
  protected def logSuccess(
      requestName: String,
      session: Session,
      start: Long,
      end: Long
  ): Unit = {
    statsEngine.logResponse(
      session.scenario,
      session.groups,
      requestName,
      start,
      end,
      OK,
      None,
      None
    )
    next ! session.markAsSucceeded
  }

  protected def logFailure(
      requestName: String,
      session: Session,
      start: Long,
      end: Long,
      message: String
  ): Unit = {
    statsEngine.logResponse(
      session.scenario,
      session.groups,
      requestName,
      start,
      end,
      KO,
      None,
      Some(message)
    )
    next ! session.markAsFailed
  }

  protected def trySfnRequest[T](request: Callable[T]): Try[T] = {
    Try(request.call())
  }
}
