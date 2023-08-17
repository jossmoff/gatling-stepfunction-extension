package dev.joss.gatling.sfn.action

import io.gatling.core.action.Action
import io.gatling.core.structure.ScenarioContext
import io.gatling.core.util.NameGen

case class CheckSucceededActionBuilder(output: Option[String])
    extends SfnActionBuilderBase
    with NameGen {
  override def build(ctx: ScenarioContext, next: Action): Action = {
    val protocol = getProtocol(ctx)
    val client = protocol.sfnClient
    CheckSucceededAction(client, ctx.coreComponents, next, genName(""), output)
  }
}
