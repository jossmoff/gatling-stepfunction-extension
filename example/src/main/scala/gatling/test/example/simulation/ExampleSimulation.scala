package gatling.test.example.simulation

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import software.amazon.awssdk.regions.Region
import dev.joss.gatlingstepfunctionextension.Predef._
import dev.joss.gatlingstepfunctionextension.protocol.SfnProtocol
import software.amazon.awssdk.auth.credentials.{
  AwsBasicCredentials,
  AwsSessionCredentials,
  StaticCredentialsProvider
}
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
  var sfnArn =
    "arn:aws:states:eu-west-1:000000000000:stateMachine:hello-world-sfn"

  val scn = scenario("SFN DSL test")
    .exec(
      sfn("Start Hello World Exection").startExecution
        .executionArn(sfnArn)
        .payload("{\"IsHelloWorldExample\": true}")
    )
    .pause(25000.milliseconds)
    .exec(sfn("Check the response").checkSucceeded)

  val requests = scn.inject {
    constantUsersPerSec(100) during (5 minutes)
  }
  setUp(requests).protocols(SfnProtocol(sfnClient))

}
