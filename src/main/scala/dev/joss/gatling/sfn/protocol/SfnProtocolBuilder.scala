package dev.joss.gatling.sfn.protocol

import software.amazon.awssdk.services.sfn.SfnClient

/**
 * SfnProtocolBuilder allows building of the Sfn protocol
 */
case object SfnProtocolBuilderBase {
  def client(client: SfnClient): SfnProtocolBuilder = SfnProtocolBuilder(client)
}

case class SfnProtocolBuilder(
    sfnClient: SfnClient
) {
  def build: SfnProtocol = SfnProtocol(sfnClient)
}
