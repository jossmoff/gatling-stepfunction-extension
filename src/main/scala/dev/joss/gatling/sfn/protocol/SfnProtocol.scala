package dev.joss.gatling.sfn.protocol

import io.gatling.core.protocol.{Protocol, ProtocolKey}
import software.amazon.awssdk.services.sfn.SfnClient

object SfnProtocol {
  val sfnProtocolKey: ProtocolKey[SfnProtocol, SfnComponents] =
    new SfnProtocolKey
}

final case class SfnProtocol(sfnClient: SfnClient) extends Protocol {
  type Components = SfnComponents
}
