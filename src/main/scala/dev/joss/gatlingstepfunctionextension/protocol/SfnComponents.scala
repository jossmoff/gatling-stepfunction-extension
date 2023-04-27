package dev.joss.gatlingstepfunctionextension.protocol

import io.gatling.core.CoreComponents
import io.gatling.core.protocol.ProtocolComponents
import io.gatling.core.session.Session

case class SfnComponents(
    coreComponents: CoreComponents,
    sfnProtocol: SfnProtocol
) extends ProtocolComponents {
  override def onStart: Session => Session = Session.Identity

  override def onExit: Session => Unit = ProtocolComponents.NoopOnExit
}
