package dev.joss.gatling.sfn.request.attributes

import io.gatling.core.session.Expression
import software.amazon.awssdk.services.sfn.model.HistoryEventType

case class SfnCheckStateAttributes(
    requestName: Expression[String],
    stateName: Expression[String],
    stateType: Expression[HistoryEventType]
)
