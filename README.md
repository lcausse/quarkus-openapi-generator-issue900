# quarkus-openapi-generator Reproducer issue #900

This repository contains a reproducer for Quarkus (https://github.com/quarkiverse/quarkus-openapi-generator/issues/900) issue.
To reproduce it, do a native build
Run in dev mode
./mvnw quarkus:dev

Get http://localhost:8080/hello endpoint 
=> It calls 5 times a non exhisting REST Server with the generated Client (with Bearer Token Auth)
- first time : 1 Authorization Header can be seen in log file ==> Correct
- other times : several values in Authorization Header can be seen in log file


## CODE GENERATOR INFO

One API json file located in src/openapi : quite complex ... but highly simplified from real one ! => Sorry not having done more
reproducer_json is the conf key
Bearer id dummy

I had an extension to the generated API to add a Request Client Filter to log Authorization Headers at each request

## VERSIONS
windows 11
Java 21
quarkus:3.17.4
quarkus-openapi-generator:2.6.0-lts
