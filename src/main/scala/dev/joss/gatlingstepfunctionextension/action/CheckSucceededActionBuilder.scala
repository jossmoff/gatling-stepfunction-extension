package dev.joss.gatlingstepfunctionextension.action

import dev.joss.gatlingstepfunctionextension.protocol.SfnProtocol
import dev.joss.gatlingstepfunctionextension.request.SfnAttributes
import io.gatling.core.action.Action
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.session.Expression
import io.gatling.core.structure.ScenarioContext
import io.gatling.core.util.NameGen

case class CheckSucceededActionBuilder()
    extends SfnActionBuilder
    with NameGen {
  override def build(ctx: ScenarioContext, next: Action): Action = {
    val protocol = getProtocol(ctx)
    val client = protocol.sfnClient
    CheckSucceededAction(client, ctx.coreComponents, next, genName(""))
  }

}
