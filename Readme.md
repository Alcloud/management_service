# Notification Management Service

This project contains the specification and the implementation of the notification management service subcomponent of the notification mechanisms of the CREDENTIAL Wallet.
The notification management service offers methods to configure the notification preferences of a user.
The project is packed in a standalone jar file which executes the webservice in an embedded jetty application.


## How to build

The project can be build with

```sh
mvn clean package
```

The executable jar is moved in the `target` subdirectory of the project.
The webservice can be executed with

```sh
java -jar target/notificationmanagementservice.jar
```

The service is available under `http://localhost:8080/v1/notificationManagementService/`

## Build docker images

The project ships with a docker configuration.
The dockerfile can be found in `./package/Dockerfile`.
The docker image can be build with the following command

```sh
docker build -t notificationmanagementservice ./package
```

Then, the docker image can be executed with

```sh
docker run -p8080:8080 notificationmanagementservice
```

The service is available under `http://localhost:8080/v1/notificationManagementService/`

## Endpoints

The following endpoints are available for the notification management service

 - getPreferences
 - addPreferences
 - deletePreferences
 - resetPreferences

The specification of the endpoints and the data model can be found in `./api.yaml`.

## Implementation

The webservice is implemented in the class `eu.credential.wallet.notificationmanagementservice.api.impl.NotificationManagementServiceApiServiceImpl.java`.

## Eclipse integration

The project is configured with the `swagger-codegen-maven-plugin` plugin.
Therefore, java classes for the data model and the endpoint implementation are generated from the api specification and are placed in the directory `./target/generated-sources/swagger/src/gen/java`.
Eclipse needs these files in a `Source Folder` in order to proper configure its workspace.
This can be done by right click on the folder and select `Build Path -> Use as Source Folder`.

## Postman examples

Examples for postman can be found here:

[![Run in Postman](https://run.pstmn.io/button.svg)](https://app.getpostman.com/run-collection/252a33c9279845d44b4b)