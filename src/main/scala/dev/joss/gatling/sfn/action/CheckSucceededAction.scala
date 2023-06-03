package dev.joss.gatling.sfn.action

import io.gatling.commons.stats.{KO, OK}
import io.gatling.commons.util.Clock
import io.gatling.core.CoreComponents
import io.gatling.core.action.{Action, ExitableAction}
import io.gatling.core.session._
import io.gatling.core.stats.StatsEngine
import software.amazon.awssdk.services.sfn.SfnClient
import software.amazon.awssdk.services.sfn.model.{
  DescribeExecutionRequest,
  DescribeExecutionResponse
}
import software.amazon.awssdk.services.sfn.model.ExecutionStatus.{
  RUNNING,
  SUCCEEDED
}

import java.time.Instant
import scala.util.Try

case class CheckSucceededAction(
    sfnClient: SfnClient,
    coreComponents: CoreComponents,
    next: Action,
    id: String
) extends SfnActionBase {

  override def name: String = "Describe Step Function Execution"
  override def statsEngine: StatsEngine = coreComponents.statsEngine
  override def clock: Clock = coreComponents.clock

  override def execute(session: Session): Unit = {
    val executionRequest: DescribeExecutionRequest.Builder =
      DescribeExecutionRequest.builder

    val executionArn: String = session("executionArn").as[String]

    executionRequest.executionArn(executionArn)

    val tryDescribeExecutionResponse: Try[DescribeExecutionResponse] =
      trySfnRequest(() => sfnClient.describeExecution(executionRequest.build()))

    if (tryDescribeExecutionResponse.isFailure) {
      val failedMessage: String =
        tryDescribeExecutionResponse.failed.get.getMessage
      logFailure(
        name,
        session,
        Instant.now().toEpochMilli,
        Instant.now().toEpochMilli,
        s"Failed to get execution description for the execution with arn: $executionArn. Reason: $failedMessage"
      )
    } else {
      logFromExecutionDescription(session, tryDescribeExecutionResponse.get)
    }

  }

  private def logFromExecutionDescription(
      session: Session,
      executionResponse: DescribeExecutionResponse
  ): Unit = {
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
        "The stepfunction could not complete within the allotted time"
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
}
