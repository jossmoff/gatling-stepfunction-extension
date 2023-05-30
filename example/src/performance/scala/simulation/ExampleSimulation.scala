package simulation

import dev.joss.gatling.sfn.Predef._
import dev.joss.gatling.sfn.protocol.{SfnProtocol, SfnProtocolBuilder}
import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.{PopulationBuilder, ScenarioBuilder}
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.auth.credentials.{AwsSessionCredentials, StaticCredentialsProvider}
import software.amazon.awssdk.http.apache.ApacheHttpClient
import software.amazon.awssdk.services.sfn.SfnClient

import java.net.URI
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
class ExampleSimulation extends Simulation {
  var sfnClient: SfnClient = SfnClient
    .builder()
    .endpointOverride(URI.create("http://localhost:4566"))
    .region(Region.EU_WEST_1)
    .credentialsProvider(
      StaticCredentialsProvider.create(
        AwsSessionCredentials.create("definitely", "real", "credentials")
      )
    )
    .httpClient(ApacheHttpClient.create())
    .build()

  var sfnProtocol: SfnProtocolBuilder = sfn.client(sfnClient)

  var sfnArn =
    "arn:aws:states:eu-west-1:000000000000:stateMachine:hello-world-sfn"

  val scn: ScenarioBuilder = scenario("SFN DSL test")
    .exec(
      sfn("Start Hello World Exection").startExecution
        .arn(sfnArn)
        .payload("{\"IsHelloWorldExample\": true}")
    )
    .pause(5000.milliseconds)
    .exec(sfn("Check the response").checkSucceeded)

  val requests: PopulationBuilder = scn.inject {
    constantUsersPerSec(1) during (5 seconds)
  }
  setUp(requests).protocols(sfnProtocol)

}
