package dev.joss.gatlingstepfunctionextension.request

import dev.joss.gatlingstepfunctionextension.action.SfnActionBuilder
import io.gatling.core.session.Expression

case class SfnBuilder(
    function: Expression[String],
    requestName: Option[Expression[String]] = None,
    payload: Option[Expression[String]] = None
) {
  def payload(payload: Expression[String]): SfnBuilder =
    copy(payload = Some(payload))

  def requestName(requestName: Expression[String]): SfnBuilder =
    copy(requestName = Some(requestName))

  def build: SfnActionBuilder =
    SfnActionBuilder(SfnAttributes(function, requestName, payload))
}
