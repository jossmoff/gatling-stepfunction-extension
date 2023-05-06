package dev.joss.gatling.sfn.protocol

import io.gatling.core.CoreComponents
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.protocol.{Protocol, ProtocolKey}
import software.amazon.awssdk.services.sfn.SfnClient

object SfnProtocol {
  val sfnProtocolKey: ProtocolKey[SfnProtocol, SfnComponents] =
    new ProtocolKey[SfnProtocol, SfnComponents] {
      def protocolClass: Class[io.gatling.core.protocol.Protocol] = classOf[SfnProtocol].asInstanceOf[Class[io.gatling.core.protocol.Protocol]]

      def defaultProtocolValue(configuration: GatlingConfiguration): SfnProtocol =
        throw new IllegalStateException("Can't provide a default value for SfnProtocol")

      def newComponents(coreComponents: CoreComponents): SfnProtocol => SfnComponents = {
        sfnProtocol => SfnComponents(coreComponents, sfnProtocol)
      }
    }
}

final case class SfnProtocol(sfnClient: SfnClient) extends Protocol {
  type Components = SfnComponents
}
