package com.horde;

import brave.Tracing;
import brave.http.HttpTracing;
import brave.jersey.server.TracingApplicationEventListener;
import org.glassfish.jersey.server.ResourceConfig;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Reporter;
import zipkin2.reporter.Sender;
import zipkin2.reporter.kafka11.KafkaSender;
import zipkin2.reporter.okhttp3.OkHttpSender;

import java.util.HashMap;
import java.util.Map;

import static com.horde.Config.ZIPKIN_HOST;

public class MyResourceConfig extends ResourceConfig {

  private static final String APPLICATION_NAME = "zipkin-sample";

  public MyResourceConfig() {

    packages("com.horde.resource");

    Reporter<Span> reporter = AsyncReporter.builder(httpSender()).build();

    Tracing tracing = Tracing.newBuilder()
        .localServiceName(APPLICATION_NAME)
        .spanReporter(reporter)
        .build();

    HttpTracing httpTracing = HttpTracing.create(tracing);
    registerInstances(TracingApplicationEventListener.create(httpTracing));
  }

  /**
   * Send spans directly to Zipkin over it's http API.
   */
  private Sender httpSender() {
    return OkHttpSender.create(ZIPKIN_HOST);
  }

  /**
   * Send spans to Kafka from which Zipkin will read.
   */
  private Sender kafkaSender() {

    // TODO put proper parameters here for your set-up
    String keyStore = "";
    String keyStorePass = "";
    String kafkaHost = "";

    Map<String, Object> properties = new HashMap<>();
    properties.put("security.protocol", "SSL");
    properties.put("ssl.truststore.location", keyStore);
    properties.put("ssl.truststore.password", keyStorePass);
    properties.put("ssl.keystore.location", keyStore);
    properties.put("ssl.keystore.password", keyStorePass);
    properties.put("ssl.key.password", keyStorePass);

    return KafkaSender.newBuilder()
        .bootstrapServers(kafkaHost)
        .overrides(properties)
        .build();
  }

}
