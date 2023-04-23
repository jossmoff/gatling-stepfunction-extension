package dev.joss.gatlingstepfunctionextension.protocol

import io.gatling.core.CoreComponents
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.protocol.{Protocol, ProtocolKey}

case class SfnProtocolKey() extends ProtocolKey[SfnProtocol, SfnComponents] {
  override def protocolClass: Class[Protocol] =
    classOf[SfnProtocol].asInstanceOf[Class[Protocol]]

  override def defaultProtocolValue(
      configuration: GatlingConfiguration
  ): SfnProtocol = throw new IllegalStateException(
    "Can't provide a default value for SfnProtocol"
  )

  override def newComponents(
      coreComponents: CoreComponents
  ): SfnProtocol => SfnComponents =
    sfnProtocol => SfnComponents(coreComponents, sfnProtocol)
}
