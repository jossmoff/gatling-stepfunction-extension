package dev.joss.gatling.sfn

import dev.joss.gatling.sfn.action.StartSyncExecutionActionBuilder
import dev.joss.gatling.sfn.protocol.{
  SfnProtocol,
  SfnProtocolBuilder,
  SfnProtocolBuilderBase
}
import dev.joss.gatling.sfn.request.{
  CheckStateSucceededDslBuilder,
  CheckSucceededDslBuilder,
  SfnDslBuilderBase,
  StartExecutionDslBuilder
}
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.session._

import scala.language.implicitConversions

trait SfnDsl {
  def sfn(implicit
      configuration: GatlingConfiguration
  ): SfnProtocolBuilderBase.type = SfnProtocolBuilderBase

  /** DSL text to start the sfn builder
    *
    * @param requestName
    *   human readable name of request
    * @return
    *   a SfnDslBuilderBase instance which can be used to build up a Sfn request
    */
  def sfn(requestName: Expression[String]): SfnDslBuilderBase =
    new SfnDslBuilderBase(requestName)

  /** Convert a SfnProtocolBuilder to a SfnProtocol <p> Simplifies the API
    * somewhat (you can pass the builder reference to the scenario
    * .protocolConfig() method)
    */
  implicit def sfnProtocolBuilder2sfnProtocol(
      builder: SfnProtocolBuilder
  ): SfnProtocol = builder.build

  implicit def sfnDslBuilder2ActionBuilder(
      builder: StartExecutionDslBuilder
  ): ActionBuilder = builder.build

  implicit def sfnDslBuilder2ActionBuilder(
      builder: StartSyncExecutionActionBuilder
  ): ActionBuilder = builder.build

  implicit def sfnDslBuilder2ActionBuilder(
      builder: CheckSucceededDslBuilder
  ): ActionBuilder = builder.build

  implicit def sfnDslBuilder2ActionBuilder(
      builder: CheckStateSucceededDslBuilder
  ): ActionBuilder = builder.build

}
