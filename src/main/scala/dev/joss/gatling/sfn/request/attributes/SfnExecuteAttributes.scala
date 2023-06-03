package dev.joss.gatling.sfn.request.attributes

import io.gatling.core.session.Expression
case class SfnExecuteAttributes(
    requestName: Expression[String],
    stateMachineArn: Expression[String],
    input: Expression[String]
)
