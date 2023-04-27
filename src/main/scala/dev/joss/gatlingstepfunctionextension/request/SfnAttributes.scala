package dev.joss.gatlingstepfunctionextension.request

import io.gatling.core.session.Expression
case class SfnAttributes(
    requestName: Expression[String],
    stateMachineArn: Expression[String],
    input: Expression[String]
)
