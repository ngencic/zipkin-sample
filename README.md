# What is this?

This is a Java application which demonstrates how you can implement distributed system tracing using
[Zipkin](https://zipkin.io/) with Jetty and Jersey. In basic set-up spans will be sent to Zipkin
over http, but there is also a sample function on how to send them over Kafka.

Main class is doing initialization of Jetty server.

MyResourceConfig class has configuration for Zipkin tracing.

Resource has defines 2 end-points where first one will trigger the second using OkHttpClient. It also
contains tracing example for OkHttpClient.

# How to run?

Launch Dockerized Zipkin server:

```
docker run -p 9411:9411 openzipkin/zipkin
```

Build & run application:

```
mvn package
mvn exec:java
```

Trigger http end-point:

```
curl localhost:8080/msg
```

Observe spans and traces, by copying this link into a browser: http://localhost:9411/zipkin/
