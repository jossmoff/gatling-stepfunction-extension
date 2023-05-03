package dev.joss.gatling.sfn.action

import dev.joss.gatling.sfn.protocol.SfnProtocol
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.structure.ScenarioContext

abstract class SfnActionBuilder extends ActionBuilder {
  protected def getProtocol(ctx: ScenarioContext): SfnProtocol = {
    ctx.protocolComponentsRegistry
      .components(SfnProtocol.sfnProtocolKey)
      .sfnProtocol
  }
}
