package dev.joss.gatlingstepfunctionextension

import dev.joss.gatlingstepfunctionextension.protocol.{SfnProtocol, SfnProtocolBuilder}
import dev.joss.gatlingstepfunctionextension.request.{
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
  ): SfnProtocolBuilder.type = SfnProtocolBuilder

  /** DSL text to start the sfn builder
    *
    * @param requestName
    *   human readable name of request
    * @return
    *   a SfnDslBuilderBase instance which can be used to build up a SFN request
    */
  def sfn(requestName: Expression[String]): SfnDslBuilderBase =
    new SfnDslBuilderBase(requestName)

  /** Convert a SfnProtocolBuilder to a SfnProtocol <p> Simplifies the API
    * somewhat
    */
  implicit def sfnProtocolBuilder2sfnProtocol(
      builder: SfnProtocolBuilder
  ): SfnProtocol = builder.build

  implicit def sfnDslBuilder2ActionBuilder(
      builder: StartExecutionDslBuilder
  ): ActionBuilder = builder.build

  implicit def sfnDslBuilder2ActionBuilder(
      builder: CheckSucceededDslBuilder
  ): ActionBuilder = builder.build

}
