package dev.joss.gatlingstepfunctionextension

import dev.joss.gatlingstepfunctionextension.protocol.{
  SfnProtocol,
  SfnProtocolBuilder
}
import dev.joss.gatlingstepfunctionextension.request.SfnBuilder

import scala.language.implicitConversions
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.session.Expression

object Predef {

  def sfn(function: Expression[String]): SfnBuilder =
    SfnBuilder(function)

  def sfn(implicit
      configuration: GatlingConfiguration
  ): SfnProtocolBuilder = SfnProtocolBuilder()

  implicit def invokeBuilderToActionBuilder(
      builder: SfnBuilder
  ): ActionBuilder = builder.build

  implicit def protocolBuilderToProtocol(
      builder: SfnProtocolBuilder
  ): SfnProtocol = builder.build
}
