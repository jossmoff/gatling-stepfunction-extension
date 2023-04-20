Sure, here's a sample README file for the custom Gatling extension for performance testing Step Functions using the AWS SDK:

# Gatling AWS Step Function Extension

This is a custom Gatling extension that allows you to perform load testing on AWS Step Functions using the AWS SDK. With this extension, you can start a Step Function execution and wait for it to complete, allowing you to test the performance of your Step Functions under load.

## Prerequisites

To use this extension, you'll need the following:

- Java 8 or later
- Gatling 3.0 or later
- AWS SDK for Java 1.11 or later

## Installation

To use this extension, you can add it as a dependency to your Gatling project. Add the following dependency to your Gatling project's `pom.xml` file:

```
<dependency>
  <groupId>dev.joss</groupId>
  <artifactId>gatling-aws-step-function-extension</artifactId>
  <version>1.0.0</version>
</dependency>
```

Alternatively, if you're using a build tool like Gradle, you can add the following dependency to your project:

```
implementation 'dev.joss:gatling-aws-step-function-extension:1.0.0'
```

## Usage

To use this extension in your Gatling simulation, you can import the `io.gatling.aws.Predef._` package and use the `awsStepFunction` DSL to create a new `AwsStepFunctionAction`. Here's an example:

```scala
import io.gatling.core.Predef._
import io.gatling.aws.Predef._
import com.amazonaws.services.stepfunctions.AWSStepFunctionsClientBuilder
import com.amazonaws.services.stepfunctions.AWSStepFunctions

class MySimulation extends Simulation {
  val awsStepFunctions: AWSStepFunctions = AWSStepFunctionsClientBuilder.defaultClient()

  val scn = scenario("MyScenario")
    .exec(
      awsStepFunction("MyStepFunction")
        .stateMachineArn("arn:aws:states:us-east-1:123456789012:stateMachine:MyStateMachine")
        .input("""{"key": "value"}""")
    )

  setUp(
    scn.inject(atOnceUsers(10))
  ).protocols(http.baseUrl("https://example.com"))
   .assertions(global.successfulRequests.percent.is(100))
}
```

This example creates a new `AwsStepFunctionAction` that starts a Step Function execution and waits for it to complete. The `stateMachineArn` parameter specifies the ARN of the Step Function state machine to execute, and the `input` parameter specifies the input to the execution.

You can customize the behavior of the `AwsStepFunctionAction` by modifying the implementation of the `AwsStepFunctionAction` class. For example, you can modify the `execute` method to include additional logging or error handling.

## License

This extension is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.