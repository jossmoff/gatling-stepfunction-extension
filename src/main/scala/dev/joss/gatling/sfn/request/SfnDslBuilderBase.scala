package dev.joss.gatling.sfn.request

import dev.joss.gatling.sfn.action.{
  CheckStateSucceededActionBuilder,
  CheckSucceededActionBuilder,
  StartExecutionActionBuilder
}
import dev.joss.gatling.sfn.request.attributes.{
  SfnCheckStateAttributes,
  SfnExecuteAttributes
}
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.session.Expression
import software.amazon.awssdk.services.sfn.model.HistoryEventType

import scala.concurrent.duration.FiniteDuration

final class SfnDslBuilderBase(requestName: Expression[String]) {
  def startExecution: StartExecutionDslBuilder.Arn =
    new StartExecutionDslBuilder.Arn(requestName)

  def checkSucceeded: CheckSucceededDslBuilder =
    CheckSucceededDslBuilder(requestName)

  def checkStateSucceeded: CheckStateSucceededDslBuilder.StateName =
    new CheckStateSucceededDslBuilder.StateName(requestName)
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
    def payload(payload: Expression[String]): MaxExecutionTime =
      new MaxExecutionTime(requestName, executionArn, payload)
  }

  final class MaxExecutionTime(
      requestName: Expression[String],
      executionArn: Expression[String],
      payload: Expression[String]
  ) {
    def maxExecutionTime(
        maxExecutionTime: Expression[FiniteDuration]
    ): StartExecutionDslBuilder =
      StartExecutionDslBuilder(
        SfnExecuteAttributes(
          requestName,
          executionArn,
          payload,
          maxExecutionTime
        ),
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

final case class CheckSucceededDslBuilder(requestName: Expression[String]) {
  def build: ActionBuilder = CheckSucceededActionBuilder()
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
