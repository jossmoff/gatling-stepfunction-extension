package dev.joss.gatling.sfn.request

import dev.joss.gatling.sfn.action.{
  CheckSucceededActionBuilder,
  StartExecutionActionBuilder
}
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.session.Expression

final class SfnDslBuilderBase(requestName: Expression[String]) {
  def startExecution: StartExecutionDslBuilder.Arn =
    new StartExecutionDslBuilder.Arn(requestName)

  def checkSucceeded: CheckSucceededDslBuilder =
    CheckSucceededDslBuilder(requestName)
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
