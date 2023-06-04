package dev.joss.gatling.sfn.action

import dev.joss.gatling.sfn.request.attributes.SfnExecuteAttributes
import io.gatling.core.action.Action
import io.gatling.core.structure.ScenarioContext
import io.gatling.core.util.NameGen

case class StartExecutionActionBuilder(attributes: SfnExecuteAttributes)
    extends SfnActionBuilderBase
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
