package dev.joss.gatlingstepfunctionextension.request

import dev.joss.gatlingstepfunctionextension.action.{
  StartExecutionActionBuilder, CheckSucceededActionBuilder}
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.session.Expression

final class SfnDslBuilderBase(requestName: Expression[String]) {
  def startExecution: StartExecutionDslBuilder.ExecutionArn =
    new StartExecutionDslBuilder.ExecutionArn(requestName)

  def checkSucceeded: CheckSucceededDslBuilder =
    CheckSucceededDslBuilder(requestName)
}

object StartExecutionDslBuilder {
  final class ExecutionArn(requestName: Expression[String]) {
    def executionArn(executionArn: Expression[String]): Payload =
      new Payload(requestName, executionArn)
  }

  final class Payload(
      requestName: Expression[String],
      executionArn: Expression[String]
  ) {
    def payload(payload: Expression[String]): StartExecutionDslBuilder =
      StartExecutionDslBuilder(
        SfnAttributes(requestName, executionArn, payload),
        StartExecutionActionBuilder
      )
  }
}

final case class StartExecutionDslBuilder(
    attributes: SfnAttributes,
    factory: SfnAttributes => StartExecutionActionBuilder
) {
  def build: ActionBuilder = factory(attributes)
}

final case class CheckSucceededDslBuilder(requestName: Expression[String]) {
  def build: ActionBuilder = CheckSucceededActionBuilder()
}
