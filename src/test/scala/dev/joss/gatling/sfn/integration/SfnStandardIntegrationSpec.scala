package dev.joss.gatling.sfn.integration

import dev.joss.gatling.sfn._
import dev.joss.gatling.sfn.protocol.SfnProtocol
import io.gatling.core.CoreDsl
import io.gatling.core.structure.ScenarioBuilder
import software.amazon.awssdk.auth.credentials.{
  AwsSessionCredentials,
  StaticCredentialsProvider
}
import software.amazon.awssdk.http.apache.ApacheHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sfn.SfnClient
import software.amazon.awssdk.services.sfn.model.HistoryEventType.WAIT_STATE_EXITED

import java.net.URI
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

class SfnStandardIntegrationSpec extends SfnSpec with CoreDsl with SfnDsl {

  var sfnClient: SfnClient = SfnClient
    .builder()
    .endpointOverride(URI.create("http://0.0.0.0:4566"))
    .region(Region.EU_WEST_1)
    .credentialsProvider(
      StaticCredentialsProvider.create(
        AwsSessionCredentials.create("definitely", "real", "credentials")
      )
    )
    .httpClient(ApacheHttpClient.create())
    .build()

  var sfnProtocol: SfnProtocol = sfn.client(sfnClient).build

  var sfnArn =
    "arn:aws:states:eu-west-1:000000000000:stateMachine:hello-world-sfn"

  "gatling-sfn" should "start and check success of a standard statemachine" in {

    val scn: ScenarioBuilder = scenario("SFN DSL test")
      .exec(
        sfn("Start Hello World Execution").startExecution
          .arn(sfnArn)
          .payload("{\"IsHelloWorldExample\": true}")
      )
      .pause(10 seconds)
      .exec(
        sfn("Check the response").checkSucceeded
      )

    val session = runScenario(scn, sfnProtocol)

    session.isFailed shouldBe false
  }

  "gatling-sfn" should "start and check success of a task success standard statemachine" in {

    val scn: ScenarioBuilder = scenario("SFN DSL test")
      .exec(
        sfn("Start Hello World Execution").startExecution
          .arn(sfnArn)
          .payload("{\"IsHelloWorldExample\": true}")
      )
      .pause(10 seconds)
      .exec(
        sfn("Check the response").checkStateSucceeded
          .stateName("Wait 3 sec")
          .stateType(WAIT_STATE_EXITED)
      )

    val session = runScenario(scn, sfnProtocol)

    session.isFailed shouldBe false
  }

  "gatling-sfn" should "fail if stepfunction has not finished executing" in {

    val scn: ScenarioBuilder = scenario("SFN DSL test")
      .exec(
        sfn("Start Hello World Execution").startExecution
          .arn(sfnArn)
          .payload("{\"IsHelloWorldExample\": true}")
      )
      .exec(
        sfn("Check the response").checkSucceeded
      )

    val session = runScenario(scn, sfnProtocol)

    session.isFailed shouldBe true
  }

  "gatling-sfn" should "fail if stepfunction task has not finished executing" in {

    val scn: ScenarioBuilder = scenario("SFN DSL test")
      .exec(
        sfn("Start Hello World Execution").startExecution
          .arn(sfnArn)
          .payload("{\"IsHelloWorldExample\": true}")
      )
      .exec(
        sfn("Check the response").checkStateSucceeded
          .stateName("Wait 3 sec")
          .stateType(WAIT_STATE_EXITED)
      )

    val session = runScenario(scn, sfnProtocol)

    session.isFailed shouldBe true
  }

  "gatling-sfn" should "fail if stepfunction task does not exist" in {

    val scn: ScenarioBuilder = scenario("SFN DSL test")
      .exec(
        sfn("Start Hello World Execution").startExecution
          .arn(sfnArn)
          .payload("{\"IsHelloWorldExample\": true}")
      )
      .pause(10 seconds)
      .exec(
        sfn("Check the response").checkStateSucceeded
          .stateName("Not a task")
          .stateType(WAIT_STATE_EXITED)
      )

    val session = runScenario(scn, sfnProtocol)

    session.isFailed shouldBe true
    print(session.groups)
  }

  "gatling-sfn" should "start and check success and output message of a standard statemachine" in {

    val scn: ScenarioBuilder = scenario("SFN DSL test")
      .exec(
        sfn("Start Hello World Execution").startExecution
          .arn(sfnArn)
          .payload("{\"IsHelloWorldExample\": true}")
      )
      .pause(10 seconds)
      .exec(
        sfn("Check the response").checkSucceededWithOutput("\"Hello World!\"")
      )

    val session = runScenario(scn, sfnProtocol)

    session.isFailed shouldBe false
  }
}
