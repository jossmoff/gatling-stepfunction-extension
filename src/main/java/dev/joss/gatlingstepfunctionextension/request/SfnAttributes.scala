package dev.joss.gatlingstepfunctionextension.request

import io.gatling.core.session.Expression
case class SfnAttributes(
    stateMachineArn: Expression[String],
    sfnName: Option[Expression[String]],
    input: Option[Expression[String]]
)
