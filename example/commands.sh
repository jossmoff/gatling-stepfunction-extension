# Create role

awslocal iam create-role \
--region eu-west-1 \
--role-name admin \
--assume-role-policy-document '{"Version":"2012-10-17","Statement":[{"Effect":"Allow","Action":"*","Resource":"*"}]}'

# Create statemachine
awslocal stepfunctions create-state-machine \
--region eu-west-1 \
--name "hello-world-sfn" \
--definition "$(cat statemachine.json)" \
--role "arn:aws:iam::000000000000:role/admin"

# Execute step function
awslocal stepfunctions start-execution \
--region eu-west-1 \
--state-machine-arn "arn:aws:states:eu-west-1:000000000000:stateMachine:hello-world-sfn" \
--input "{\"IsHelloWorldExample\": true}"

# awslocal stepfunctions get-execution-history --execution-arn "arn:aws:states:eu-west-1:000000000000:execution:hello-world-sfn:6cedcd2b-d6cc-4c8c-9df9-102e6bfb87aa"