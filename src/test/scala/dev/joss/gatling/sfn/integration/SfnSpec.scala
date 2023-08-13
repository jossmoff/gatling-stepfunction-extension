package dev.joss.gatling.sfn.integration

import scala.concurrent.Await
import scala.concurrent.duration._
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}

import scala.concurrent.duration._
import io.gatling.commons.util.DefaultClock
import io.gatling.core.CoreComponents
import io.gatling.core.action.{Action, ActorDelegatingAction}
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.pause.Constant
import io.gatling.core.protocol.{
  Protocol,
  ProtocolComponentsRegistries,
  Protocols
}
import io.gatling.core.session.{Session, StaticValueExpression}
import io.gatling.core.stats.StatsEngine
import io.gatling.core.structure.{ScenarioBuilder, ScenarioContext}
import akka.actor.ActorRef
import dev.joss.gatling.sfn._
import dev.joss.gatling.sfn.base.AkkaSpec
import dev.joss.gatling.sfn.protocol.SfnProtocol
import io.netty.channel.EventLoopGroup

trait SfnSpec extends AkkaSpec with SfnDsl {

  implicit val configuration: GatlingConfiguration =
    GatlingConfiguration.loadForTest()

  @SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
  def runScenario(
      sb: ScenarioBuilder,
      protocol: SfnProtocol,
      timeout: FiniteDuration = 60.seconds
  )(implicit
      configuration: GatlingConfiguration
  ): Session = {
    val clock = new DefaultClock
    val coreComponents =
      new CoreComponents(
        system,
        mock[EventLoopGroup],
        mock[ActorRef],
        None,
        mock[StatsEngine],
        clock,
        mock[Action],
        configuration
      )
    val next: Action = new ActorDelegatingAction("next", self)
    val protocolComponentsRegistry =
      new ProtocolComponentsRegistries(
        coreComponents,
        Protocol.indexByType(Seq(protocol))
      )
        .scenarioRegistry(Map.empty)

    val scenarioContext = new ScenarioContext(
      coreComponents,
      protocolComponentsRegistry,
      Constant,
      throttled = false
    )
    val actor = sb.actionBuilders.foldLeft(next) { (chainNext, actionBuilder) =>
      actionBuilder.build(scenarioContext, chainNext)
    }

    actor ! emptySession
    val session = expectMsgClass(timeout, classOf[Session])

    session
  }
}
