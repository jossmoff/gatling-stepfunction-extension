# Gatling AWS Step Function Extension

This is a custom Gatling extension that allows you to perform load testing on AWS Step Functions using the AWS SDK. With this extension, you can start a Step Function execution and wait for it to complete, allowing you to test the performance of your Step Functions under load.

## Prerequisites

To use this extension, you'll need the following:

- Java 8 or later
- Gatling 3.0
- AWS SDK

## Installation

To use this extension, you can add it as a dependency to your Gatling project. Add the following dependency to your Gatling project's `build.gradle`:

```
implementation 'dev.joss:gatling-stepfunction-extension:1.3.1'
```
For other build tools see the maven central repository [overview](https://central.sonatype.com/artifact/dev.joss/gatling-stepfunction-extension/1.3.1/overview#Overview).

## Usage

To use this extension in your Gatling simulation, you can import the `dev.joss.gatling.sfn.Predef._` package and use the `Sfn` DSL to create a new simulation. Here's an example:

```scala
import dev.joss.gatling.sfn.Predef._
import dev.joss.gatling.sfn.protocol.SfnProtocol
import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation

import software.amazon.awssdk.services.sfn.SfnClient

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

class ExampleSimulation extends Simulation {
  var sfnClient: SfnClient = SfnClient.builder().build()
  var sfnArn =
    "arn:aws:states:eu-west-1:000000000000:stateMachine:some-arn"

  val scn = scenario("SFN DSL test")
    .exec(
      sfn("Start Hello World Execution").startExecution
        .arn(sfnArn)
        .payload("{}")
    )
    .pause(5000.milliseconds)
    .exec(sfn("Check the response").checkSucceeded)

  val requests = scn.inject {
    constantUsersPerSec(100) during (5 minutes)
  }
  setUp(requests).protocols(SfnProtocol(sfnClient))

}

```

This example creates a new Gatling scenario that starts an execution, waits 5 seconds and then checks the step function has succeeded.
For a more applied example, see the `example` project which tests AWS's Hello World step function using localstack.
## License

This extension is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
