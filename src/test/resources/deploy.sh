statemachineDir=src/test/resources/statemachines

# Create role
awslocal iam create-role \
--region eu-west-1 \
--role-name admin \
--assume-role-policy-document '{"Version":"2012-10-17","Statement":[{"Effect":"Allow","Action":"*","Resource":"*"}]}'

# Create statemachine
awslocal stepfunctions create-state-machine \
--region eu-west-1 \
--name "hello-world-sfn" \
--definition "$(cat $statemachineDir/hello-world-statemachine.json)" \
--role "arn:aws:iam::000000000000:role/admin"