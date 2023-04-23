package dev.joss.gatlingstepfunctionextension.protocol

import software.amazon.awssdk.services.sfn.SfnClient

case class SfnProtocolBuilder(
    sfnClient: SfnClient = SfnClient.create()
) {
  def client(client: SfnClient): SfnProtocolBuilder =
    copy(sfnClient = client)
  def build: SfnProtocol = SfnProtocol(sfnClient)
}
