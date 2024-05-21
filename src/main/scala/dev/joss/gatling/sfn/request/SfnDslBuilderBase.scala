package dev.joss.gatling.sfn.request

import dev.joss.gatling.sfn.action.{
  CheckStateSucceededActionBuilder,
  CheckSucceededActionBuilder,
  StartExecutionActionBuilder,
  StartSyncExecutionActionBuilder
}
import dev.joss.gatling.sfn.request.attributes.{
  SfnCheckStateAttributes,
  SfnExecuteAttributes
}
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.session.Expression
import software.amazon.awssdk.services.sfn.model.HistoryEventType

final class SfnDslBuilderBase(requestName: Expression[String]) {
  def startExecution: StartExecutionDslBuilder.Arn =
    new StartExecutionDslBuilder.Arn(requestName)
  def startSyncExecution: StartSyncExecutionDslBuilder.Arn =
    new StartSyncExecutionDslBuilder.Arn(requestName)

  def checkSucceeded: CheckSucceededDslBuilder =
    CheckSucceededDslBuilder(requestName, None)

  def checkSucceededWithOutput(output: String): CheckSucceededDslBuilder =
    CheckSucceededDslBuilder(requestName, Some(output))

  def checkStateSucceeded: CheckStateSucceededDslBuilder.StateName =
    new CheckStateSucceededDslBuilder.StateName(requestName)
}

object StartSyncExecutionDslBuilder {
  final class Arn(requestName: Expression[String]) {
    def arn(arn: Expression[String]): Payload =
      new Payload(requestName, arn)
  }

  final class Payload(
   requestName: Expression[String],
   executionArn: Expression[String]
  ) {
    def payload(payload: Expression[String]): StartSyncExecutionDslBuilder =
      StartSyncExecutionDslBuilder(
        SfnExecuteAttributes(requestName, executionArn, payload),
        StartSyncExecutionActionBuilder
      )
  }
}

final case class StartSyncExecutionDslBuilder(
   attributes: SfnExecuteAttributes,
   factory: SfnExecuteAttributes => StartSyncExecutionActionBuilder
) {
  def build: ActionBuilder = factory(attributes)
}

object StartExecutionDslBuilder {
  final class Arn(requestName: Expression[String]) {
    def arn(arn: Expression[String]): Payload =
      new Payload(requestName, arn)
  }

  final class Payload(
      requestName: Expression[String],
      executionArn: Expression[String]
  ) {
    def payload(payload: Expression[String]): StartExecutionDslBuilder =
      StartExecutionDslBuilder(
        SfnExecuteAttributes(requestName, executionArn, payload),
        StartExecutionActionBuilder
      )
  }
}

final case class StartExecutionDslBuilder(
    attributes: SfnExecuteAttributes,
    factory: SfnExecuteAttributes => StartExecutionActionBuilder
) {
  def build: ActionBuilder = factory(attributes)
}

final case class CheckSucceededDslBuilder(
    requestName: Expression[String],
    output: Option[String]
) {
  def build: ActionBuilder = CheckSucceededActionBuilder(output)
}

object CheckStateSucceededDslBuilder {
  final class StateName(
      requestName: Expression[String]
  ) {
    def stateName(stateName: Expression[String]): StateType =
      new StateType(requestName, stateName)
  }

  final class StateType(
      requestName: Expression[String],
      stateName: Expression[String]
  ) {
    def stateType(
        stateType: Expression[HistoryEventType]
    ): CheckStateSucceededDslBuilder =
      CheckStateSucceededDslBuilder(
        SfnCheckStateAttributes(requestName, stateName, stateType),
        CheckStateSucceededActionBuilder
      )
  }
}

final case class CheckStateSucceededDslBuilder(
    attributes: SfnCheckStateAttributes,
    factory: SfnCheckStateAttributes => CheckStateSucceededActionBuilder
) {
  def build: ActionBuilder = factory(attributes)
}
