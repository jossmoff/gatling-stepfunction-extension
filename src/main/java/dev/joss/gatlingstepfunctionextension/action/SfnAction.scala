package dev.joss.gatlingstepfunctionextension.action

import dev.joss.gatlingstepfunctionextension.request.SfnAttributes
import org.awaitility.Awaitility.await
import io.gatling.commons.stats.{KO, OK}
import io.gatling.commons.validation.Validation
import io.gatling.core.action.{Action, ExitableAction}
import io.gatling.core.session._
import io.gatling.core.util.NameGen
import io.gatling.commons.util.Clock
import io.gatling.core.CoreComponents
import io.gatling.core.Predef.{
  value2Expression,
  value2NoUnexpectedValidationLifting
}
import io.gatling.core.stats.StatsEngine
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.sfn.SfnClient
import software.amazon.awssdk.services.sfn.model.{
  DescribeExecutionRequest,
  DescribeExecutionResponse,
  ExecutionStatus,
  StartExecutionRequest,
  StartExecutionResponse
}
import software.amazon.awssdk.services.sfn.model.ExecutionStatus.{
  RUNNING,
  SUCCEEDED
}

import java.time.Instant.now
import java.util.concurrent.Callable
import java.util.function.Predicate
import scala.concurrent.duration.{FiniteDuration, SECONDS}
import scala.concurrent.{ExecutionContext, Future}
case class SfnAction(
    sfnClient: SfnClient,
    coreComponents: CoreComponents,
    next: Action,
    id: String,
    attr: SfnAttributes
) extends ExitableAction {

  override def name: String = "Step Function Execution"

  override def execute(session: Session): Unit = {
    val executionArn = startExecution(session)
    val executionResponse = awaitSfnSuccessfulCompletion(session, executionArn)
    logSuccess(
      executionArn,
      session,
      executionResponse.startDate().toEpochMilli,
      executionResponse.stopDate().toEpochMilli
    )

  }

  private def startExecution(
      session: Session
  ): String = {
    var stateMachineArn: String = ""
    val request = StartExecutionRequest.builder()
    attr.stateMachineArn(session).map { arn =>
      stateMachineArn = arn
    }

    if (attr.sfnName.isDefined) {
      attr.sfnName
        .get(session)
        .map(name => request.name(name))
    }

    if (attr.input.isDefined) {
      attr.input
        .get(session)
        .map(payload => request.input(payload))
    }
    val start = clock.nowMillis
    val startExecutionResponse =
      makeRequest(() => sfnClient.startExecution(request.build()))
    val end = clock.nowMillis

    val executionArn = conditionallyExtractValueFromSomeResponse(
      startExecutionResponse,
      (response: StartExecutionResponse) => response.executionArn(),
      (arn: String) => arn.isEmpty
    )
    if (startExecutionResponse.isEmpty) {
      logFailure(
        stateMachineArn,
        session,
        start,
        end,
        s"Response: ${startExecutionResponse.toString}"
      )
    }
    executionArn.get
  }
  private def awaitSfnSuccessfulCompletion(
      session: Session,
      executionArn: String = null
  ): DescribeExecutionResponse = {
    val supplier: Callable[ExecutionStatus] = () => {
      describeExecution(executionArn).status()
    }
    val predicate: Predicate[ExecutionStatus] = (status: ExecutionStatus) => {
      !status.equals(RUNNING)
    }
    val start = clock.nowMillis
    val someExecutionStatus =
      makeRequest(() =>
        await()
          .atMost(10L, SECONDS)
          .pollInterval(1L, SECONDS)
          .until(supplier, predicate)
      )
    val end = clock.nowMillis

    val status = conditionallyExtractValueFromSomeResponse(
      someExecutionStatus,
      (status: ExecutionStatus) => status,
      (status: ExecutionStatus) => status.equals(SUCCEEDED)
    )

    if (status.isEmpty) {
      logFailure(
        executionArn,
        session,
        start,
        end,
        s"Returned status: $status"
      )
    }
    describeExecution(executionArn)
  }

  private def describeExecution(
      executionArn: String
  ): DescribeExecutionResponse = {
    val executionRequest =
      DescribeExecutionRequest.builder.executionArn(executionArn).build
    sfnClient.describeExecution(executionRequest)
  }

  private def makeRequest[T](request: Callable[T]): Option[T] = {
    try {
      Some(request.call())
    } catch {
      case t: Throwable => None
    }
  }

  private def conditionallyExtractValueFromSomeResponse[T, U](
      someResponse: Option[T],
      extractionMethod: T => U,
      condition: U => Boolean
  ): Option[U] = {
    if (someResponse.isDefined) {
      val response = someResponse.get
      val value = extractionMethod(response)
      if (condition(value))
        return Some(value)
    }
    None
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
