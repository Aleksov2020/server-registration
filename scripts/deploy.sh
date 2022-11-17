#!/usr/bin/env bash

mvn clean package

echo 'Copy files'

scp -i ~/.ssh/id_rsa C:/Users/aleks/IdeaProjects/bes-proebov/server/target/ServerRegistration-1.0.jar root@194.67.105.170:/root

echo "Bye"