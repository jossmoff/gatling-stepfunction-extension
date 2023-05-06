package dev.joss.gatling.sfn

import dev.joss.gatling.sfn.Predef._
import dev.joss.gatling.sfn.protocol.SfnProtocol
import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import software.amazon.awssdk.services.sfn.SfnClient

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

class SfnCompileTest extends Simulation {
  var sfnClient: SfnClient = SfnClient
    .builder().build()
  var sfnArn =
    "some-test-arn"

  var sfnProtocol = sfn.client(sfnClient)

  val scn = scenario("SFN DSL test")
    .exec(
      sfn("Start an Execution").startExecution
        .executionArn(sfnArn)
        .payload("{}")
    )
    .pause(5000.milliseconds)
    .exec(sfn("Check the response is success").checkSucceeded)

  val requests = scn.inject {
    constantUsersPerSec(100) during (5 minutes)
  }
  setUp(requests).protocols(sfnProtocol)

}
