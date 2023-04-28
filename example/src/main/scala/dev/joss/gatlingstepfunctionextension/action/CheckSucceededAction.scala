package dev.joss.gatlingstepfunctionextension.action

import dev.joss.gatlingstepfunctionextension.request.SfnAttributes
import io.gatling.commons.stats.{KO, OK}
import io.gatling.commons.util.Clock
import io.gatling.core.CoreComponents
import io.gatling.core.action.{Action, ExitableAction}
import io.gatling.core.session.{Expression, _}
import io.gatling.core.stats.StatsEngine
import software.amazon.awssdk.services.sfn.SfnClient
import software.amazon.awssdk.services.sfn.model.ExecutionStatus.{RUNNING, SUCCEEDED}
import software.amazon.awssdk.services.sfn.model.{DescribeExecutionRequest, DescribeExecutionResponse, ExecutionStatus, StartExecutionRequest}

import java.time.Instant

case class CheckSucceededAction(
    sfnClient: SfnClient,
    coreComponents: CoreComponents,
    next: Action,
    id: String
) extends ExitableAction {

  override def name: String = "Describe Step Function Execution"

  override def statsEngine: StatsEngine = coreComponents.statsEngine

  override def clock: Clock = coreComponents.clock

  override def execute(session: Session): Unit = {
    val executionRequest = DescribeExecutionRequest.builder

    val executionArn = session("executionArn").as[String]

    executionRequest.executionArn(executionArn)
    val executionResponse =
      sfnClient.describeExecution(executionRequest.build())

    if (executionResponse.status().equals(SUCCEEDED)) {
      logSuccess(
        name,
        session,
        executionResponse.startDate().toEpochMilli,
        executionResponse.stopDate().toEpochMilli
      )
    } else if (executionResponse.status().equals(RUNNING)) {
      logFailure(
        name,
        session,
        executionResponse.startDate().toEpochMilli,
        Instant.now().toEpochMilli,
        "Could not complete within the allotted time"
      )

    } else {
      logFailure(
        name,
        session,
        executionResponse.startDate().toEpochMilli,
        Instant.now().toEpochMilli,
        "The step function failed"
      )
    }

  }
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
    next ! session.markAsSucceeded
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
