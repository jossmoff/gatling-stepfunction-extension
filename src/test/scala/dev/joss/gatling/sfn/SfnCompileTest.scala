package dev.joss.gatling.sfn

import dev.joss.gatling.sfn.Predef._
import dev.joss.gatling.sfn.protocol.{SfnProtocol, SfnProtocolBuilder}
import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.{PopulationBuilder, ScenarioBuilder}
import software.amazon.awssdk.services.sfn.SfnClient

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

class SfnCompileTest extends Simulation {
  var sfnClient: SfnClient = SfnClient
    .builder()
    .build()
  var sfnArn =
    "some-test-arn"

  var sfnProtocol: SfnProtocolBuilder = sfn.client(sfnClient)

  val scn: ScenarioBuilder = scenario("SFN DSL test")
    .exec(
      sfn("Start an Execution").startExecution
        .arn(sfnArn)
        .payload("{}")
    )
    .pause(5000.milliseconds)
    .exec(sfn("Check the response is success").checkSucceeded)

  val requests: PopulationBuilder = scn.inject {
    constantUsersPerSec(100) during (5 minutes)
  }
  setUp(requests).protocols(sfnProtocol)

}
