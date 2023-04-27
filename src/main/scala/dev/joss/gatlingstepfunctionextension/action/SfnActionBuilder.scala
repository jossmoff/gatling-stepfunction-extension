package dev.joss.gatlingstepfunctionextension.action

import dev.joss.gatlingstepfunctionextension.protocol.{
  SfnComponents,
  SfnProtocol
}
import dev.joss.gatlingstepfunctionextension.request.SfnAttributes
import io.gatling.core.action.Action
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.protocol.ProtocolComponentsRegistry
import io.gatling.core.session.Expression
import io.gatling.core.structure.ScenarioContext

abstract class SfnActionBuilder extends ActionBuilder {
  protected def getProtocol(ctx: ScenarioContext) = {
    ctx.protocolComponentsRegistry
      .components(SfnProtocol.sfnProtocolKey)
      .sfnProtocol
  }
}
