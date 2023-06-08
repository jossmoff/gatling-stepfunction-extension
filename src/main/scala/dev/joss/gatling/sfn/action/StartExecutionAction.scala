package dev.joss.gatling.sfn.action

import dev.joss.gatling.sfn.request.attributes.SfnExecuteAttributes
import io.gatling.commons.stats.{KO, OK}
import io.gatling.commons.util.Clock
import io.gatling.core.CoreComponents
import io.gatling.core.action.{Action, ExitableAction}
import io.gatling.core.session._
import io.gatling.core.stats.StatsEngine
import software.amazon.awssdk.services.sfn.SfnClient
import software.amazon.awssdk.services.sfn.model.StartExecutionRequest

import java.util.concurrent.Callable
import scala.util.Try

case class StartExecutionAction(
    sfnClient: SfnClient,
    coreComponents: CoreComponents,
    next: Action,
    id: String,
    attributes: SfnExecuteAttributes
) extends SfnActionBase {
  override def statsEngine: StatsEngine = coreComponents.statsEngine
  override def clock: Clock = coreComponents.clock
  override def name: String = "Step Function Execution"

  override def execute(session: Session): Unit = {

    val executionArn: Option[String] = startExecution(session)
    if (executionArn.isDefined) {
      next ! session.set("executionArn", executionArn.get)
    }
  }

  private def startExecution(
      session: Session
  ): Option[String] = {
    val request = StartExecutionRequest.builder()

    var stateMachineArn = ""
    attributes.stateMachineArn(session).map { arn =>
      request.stateMachineArn(arn)
      stateMachineArn = arn
    }

    attributes.input(session).map { arn =>
      request.input(arn)
    }

    val start = clock.nowMillis
    val tryStartExecutionResponse =
      trySfnRequest(() => sfnClient.startExecution(request.build()))
    val end = clock.nowMillis

    if (tryStartExecutionResponse.isFailure) {
      val failedMessage: String =
        tryStartExecutionResponse.failed.get.getMessage
      logFailure(
        name,
        session,
        start,
        end,
        s"Could not start step function with ARN: $stateMachineArn. Reason: $failedMessage"
      )
      return None
    }
    Some(tryStartExecutionResponse.get.executionArn())
  }
}
