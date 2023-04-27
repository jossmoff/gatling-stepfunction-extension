package dev.joss.gatlingstepfunctionextension

import dev.joss.gatlingstepfunctionextension.protocol.{
  SfnProtocol,
  SfnProtocolBuilder
}
import dev.joss.gatlingstepfunctionextension.request.{
  CheckSucceededDslBuilder,
  SfnDslBuilderBase,
  StartExecutionDslBuilder
}
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.session._

trait SfnDsl {
  def sfn(implicit
      configuration: GatlingConfiguration
  ): SfnProtocolBuilder.type = SfnProtocolBuilder

  /** DSL text to start the jms builder
    *
    * @param requestName
    *   human readable name of request
    * @return
    *   a JmsDslBuilderBase instance which can be used to build up a JMS action
    */
  def sfn(requestName: Expression[String]): SfnDslBuilderBase =
    new SfnDslBuilderBase(requestName)

  /** Convert a JmsProtocolBuilder to a JmsProtocol <p> Simplifies the API
    * somewhat (you can pass the builder reference to the scenario
    * .protocolConfig() method)
    */
  implicit def sfnProtocolBuilder2jmsProtocol(
      builder: SfnProtocolBuilder
  ): SfnProtocol = builder.build

  implicit def sfnDslBuilder2ActionBuilder(
      builder: StartExecutionDslBuilder
  ): ActionBuilder = builder.build

  implicit def sfnDslBuilder2ActionBuilder(
      builder: CheckSucceededDslBuilder
  ): ActionBuilder = builder.build

}
