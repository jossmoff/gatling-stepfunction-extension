package uk.co.capitalone.services.decision.processor.utils

import dev.joss.gatling.sfn.action.SfnActionBase
import dev.joss.gatling.sfn.request.attributes.SfnExecuteAttributes
import io.gatling.commons.util.Clock
import io.gatling.core.CoreComponents
import io.gatling.core.action.Action
import io.gatling.core.session._
import io.gatling.core.stats.StatsEngine
import software.amazon.awssdk.services.sfn.SfnClient
import software.amazon.awssdk.services.sfn.model.StartSyncExecutionRequest

case class StartSyncExecutionAction(
    sfnClient: SfnClient,
    coreComponents: CoreComponents,
    next: Action,
    id: String,
    attributes: SfnExecuteAttributes
) extends SfnActionBase {
  override def statsEngine: StatsEngine = coreComponents.statsEngine
  override def clock: Clock = coreComponents.clock
  override def name: String = "Step Function Sync Execution"
  override def execute(session: Session): Unit = {
    startSyncExecution(session)
  }

  private def startSyncExecution(
      session: Session
  ): Unit = {
    val request = StartSyncExecutionRequest.builder()

    var stateMachineArn = ""
    attributes.stateMachineArn(session).map { arn =>
      request.stateMachineArn(arn)
      stateMachineArn = arn
    }

    attributes.input(session).map { arn =>
      request.input(arn)
    }

    val tryStartExecutionResponse =
      trySfnRequest(() => sfnClient.startSyncExecution(request.build()))

    val startTime = tryStartExecutionResponse.get.startDate().toEpochMilli
    val endTime = tryStartExecutionResponse.get.stopDate().toEpochMilli
    if (tryStartExecutionResponse.get.status().toString.equals("SUCCEEDED")) {
      logSuccess(
        name,
        session,
        startTime,
        endTime,
        "The step function successfully completed!"
      )
    } else {
      val failedMessage: String =
        tryStartExecutionResponse.failed.get.getMessage
      logFailure(
        name,
        session,
        startTime,
        endTime,
        s"Could not execute step function with ARN: $stateMachineArn. Reason: $failedMessage"
      )
    }
  }
}
