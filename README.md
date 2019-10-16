# Java Project - Revolute
Fund transfer restful API.

## Project Background
The Funds Transfer Service (FTS) is a REST application for transfering funds between accounts. 
The service provides two distinct interfaces. One is an account interface, used by dozens of client systems 
to create customer account. The other is a transfer interface invoked by multiple systems and services on behalf of end users.

Features
1. It is simple and to the point with no authentication.
2. It is a multi-thread the API ready to be invoked by multiple systems and services on behalf of end users.
4. Total fat jar is stil a tiny 2Mb with no heavy frameworks.
5. The datastore run in-memorys using Map collections.
6. The final binary requires no installation or configurations.

### Building and running the application
1. Install Java JDK 8 and optional gradle if not already installed. Running gradlew or ./gradlew can download gradle.
2. Invoke this on console
```cmd
gradle clean build
```
3. After build, invoke this command
```cmd
java -jar revolute.jar
```

and lunch http://localhost:4132


### REST API

The application currently supports the following REST APIs:
* GET /
* GET /ping
* GET /accounts
* GET /accounts/{id}
* GET /accounts/{id}/events
* POST /accounts
* POST /transfer/ping

## Assignment Objectives
Feature 
0. Keep it simple
1. Lightweight Fund transfer API
2. Solve concurrent issue
4. TDD Approach with high quality code
