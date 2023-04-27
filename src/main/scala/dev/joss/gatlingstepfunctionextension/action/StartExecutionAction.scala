package dev.joss.gatlingstepfunctionextension.action

import dev.joss.gatlingstepfunctionextension.request.SfnAttributes
import io.gatling.commons.stats.{KO, OK}
import io.gatling.commons.util.Clock
import io.gatling.core.CoreComponents
import io.gatling.core.action.{Action, ExitableAction}
import io.gatling.core.session._
import io.gatling.core.stats.StatsEngine
import org.awaitility.Awaitility.await
import software.amazon.awssdk.services.sfn.SfnClient
import software.amazon.awssdk.services.sfn.model.ExecutionStatus.{
  RUNNING,
  SUCCEEDED
}
import software.amazon.awssdk.services.sfn.model.{
  DescribeExecutionRequest,
  DescribeExecutionResponse,
  ExecutionStatus,
  StartExecutionRequest
}

import java.time.Instant
import java.util.concurrent.Callable
import java.util.function.Predicate
import scala.concurrent.duration.SECONDS

case class StartExecutionAction(
    sfnClient: SfnClient,
    coreComponents: CoreComponents,
    next: Action,
    id: String,
    attr: SfnAttributes
) extends ExitableAction {

  override def name: String = "Step Function Execution"

  override def execute(session: Session): Unit = {

    val executionArn = startExecution(session)
    next ! session.set("executionArn", executionArn)

  }

  private def startExecution(
      session: Session
  ): String = {
    val request = StartExecutionRequest.builder()

    var stateMachineArn = ""
    attr.stateMachineArn(session).map { arn =>
      request.stateMachineArn(arn)
      stateMachineArn = arn
    }

    attr.input(session).map { arn =>
      request.input(arn)
    }
    val start = clock.nowMillis
    val startExecutionResponse =
      makeRequest(() => sfnClient.startExecution(request.build()))
    val end = clock.nowMillis

    if (startExecutionResponse.isEmpty) {
      logFailure(
        "a",
        session,
        start,
        end,
        s"Could not start step function with ARN: ${stateMachineArn}"
      )
    }
    startExecutionResponse.get.executionArn()
  }

  private def makeRequest[T](request: Callable[T]): Option[T] = {
    try {
      Some(request.call())
    } catch {
      case t: Throwable => None
    }
  }

  override def statsEngine: StatsEngine = coreComponents.statsEngine

  override def clock: Clock = coreComponents.clock

  private def logSuccess(
      sfnName: String,
      session: Session,
      start: Long,
      end: Long
  ): Unit = {
    statsEngine.logResponse(
      session.scenario,
      session.groups,
      sfnName,
      start,
      end,
      OK,
      None,
      None
    )
  }

  private def logFailure(
      sfnName: String,
      session: Session,
      start: Long,
      end: Long,
      message: String
  ): Unit = {
    statsEngine.logResponse(
      session.scenario,
      session.groups,
      sfnName,
      start,
      end,
      KO,
      None,
      Some(message)
    )
    next ! session.markAsFailed
  }
}
