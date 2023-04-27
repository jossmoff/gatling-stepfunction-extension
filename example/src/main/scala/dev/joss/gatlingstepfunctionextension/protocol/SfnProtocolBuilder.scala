package dev.joss.gatlingstepfunctionextension.protocol

import software.amazon.awssdk.services.sfn.SfnClient

case class SfnProtocolBuilder(
    sfnClient: SfnClient
) {
  def build: SfnProtocol = SfnProtocol(sfnClient)
}
