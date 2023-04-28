package dev.joss.gatlingstepfunctionextension.action

import dev.joss.gatlingstepfunctionextension.request.SfnAttributes
import io.gatling.core.action.Action
import io.gatling.core.structure.ScenarioContext
import io.gatling.core.util.NameGen

case class StartExecutionActionBuilder(attributes: SfnAttributes)
    extends SfnActionBuilder
    with NameGen {
  override def build(ctx: ScenarioContext, next: Action): Action = {
    val protocol = getProtocol(ctx)
    val client = protocol.sfnClient
    StartExecutionAction(
      client,
      ctx.coreComponents,
      next,
      genName(""),
      attributes
    )
  }

}
