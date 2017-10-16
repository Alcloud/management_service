#!/bin/sh

set -e  

# if $variable is not set, then default to $value
export server_port=${server_port:-8080}

# set mongodb variables
export mongodb_host=${mongodb_host:-localhost}
export mongodb_port=${mongodb_port:-27017}

echo "Creating config files using confd"
/usr/local/bin/confd -onetime -backend env

#echo "Starting REST Service"
java -jar /tmp/notificationmanagementservice.jar -conf /tmp/service.properties -s
