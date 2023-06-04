package dev.joss.gatling.sfn.action

import dev.joss.gatling.sfn.request.attributes.SfnCheckStateAttributes
import io.gatling.commons.util.Clock
import io.gatling.core.CoreComponents
import io.gatling.core.action.Action
import io.gatling.core.session._
import io.gatling.core.stats.StatsEngine
import software.amazon.awssdk.services.sfn.SfnClient
import software.amazon.awssdk.services.sfn.model.{
  GetExecutionHistoryRequest,
  GetExecutionHistoryResponse,
  HistoryEvent,
  HistoryEventType
}

import java.time.Instant
import scala.jdk.CollectionConverters.CollectionHasAsScala
import scala.util.Try
case class CheckStateSucceededAction(
    sfnClient: SfnClient,
    coreComponents: CoreComponents,
    next: Action,
    id: String,
    attributes: SfnCheckStateAttributes
) extends SfnActionBase {
  override def statsEngine: StatsEngine = coreComponents.statsEngine
  override def clock: Clock = coreComponents.clock
  override def name: String = "Step Function Task Execution Completion"

  override def execute(session: Session): Unit = {

    val executionArn: String = session("executionArn").as[String]
    val stateName: String = attributes.stateName(session).toOption.get
    val stateType: HistoryEventType = attributes.stateType(session).toOption.get

    val executionHistoryRequest = GetExecutionHistoryRequest.builder
    executionHistoryRequest.executionArn(executionArn)

    val tryExecutionHistoryResponse: Try[GetExecutionHistoryResponse] =
      trySfnRequest(() =>
        sfnClient.getExecutionHistory(executionHistoryRequest.build())
      )

    if (tryExecutionHistoryResponse.isFailure) {
      val failedMessage: String =
        tryExecutionHistoryResponse.failed.get.getMessage
      logFailure(
        name,
        session,
        Instant.now().toEpochMilli,
        Instant.now().toEpochMilli,
        s"Could not get the execution history for the execution with arn: $executionArn. Reason: $failedMessage"
      )
    } else {
      val executionHistoryEvents: Seq[HistoryEvent] =
        tryExecutionHistoryResponse.get.events.asScala.toList
      val tryGetStateExitedEvent: Try[HistoryEvent] =
        getTaskExecutionCompleteState(
          stateName,
          stateType,
          executionHistoryEvents
        )

      val startTime: Long = executionHistoryEvents.head.timestamp.toEpochMilli

      if (tryGetStateExitedEvent.isFailure) {
        val failedMessage: String = tryGetStateExitedEvent.failed.get.getMessage
        logFailure(
          name,
          session,
          startTime,
          Instant.now().toEpochMilli,
          s"The task $stateName has not finished or a previous task failed. Reason: $failedMessage"
        )
      } else {
        val taskExitedEvent: HistoryEvent = tryGetStateExitedEvent.get
        val endTime: Long = taskExitedEvent.timestamp.toEpochMilli
        logSuccess(
          name,
          session,
          startTime,
          endTime
        )
      }
    }
  }

  private def getTaskExecutionCompleteState(
      task: String,
      stateType: HistoryEventType,
      sfnExecutionEvents: Seq[HistoryEvent]
  ): Try[HistoryEvent] = {
    Try(
      sfnExecutionEvents
        .filter(event =>
          event.`type`.equals(
            stateType
          ) && event.stateExitedEventDetails.name.equals(task)
        )
        .head
    )
  }
}
