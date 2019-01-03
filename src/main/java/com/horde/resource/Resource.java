package com.horde.resource;

import brave.Tracing;
import brave.http.HttpTracing;
import brave.okhttp3.TracingInterceptor;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Reporter;
import zipkin2.reporter.okhttp3.OkHttpSender;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import static com.horde.Config.ZIPKIN_HOST;

@Path("msg")
public class Resource {

  private static OkHttpClient httpClient;

  static {
    httpClient = constructTracedClient();
  }

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String getMessage() throws Exception {

    Request request = new Request.Builder()
        .url("http://localhost:8080/msg/msg2")
        .build();

    Response response = httpClient.newCall(request).execute();

    return "My message\n" + response.body().string();
  }

  @GET
  @Path("/msg2")
  @Produces(MediaType.TEXT_PLAIN)
  public String getMessage2() throws Exception {
    return "My message 2\n";
  }

  private static OkHttpClient constructTracedClient() {
    Reporter<Span> reporter = AsyncReporter.builder(OkHttpSender.create(ZIPKIN_HOST)).build();

    Tracing tracing = Tracing.newBuilder()
        .localServiceName("my-client")
        .spanReporter(reporter)
        .build();

    HttpTracing httpTracing = HttpTracing.create(tracing);

    return new OkHttpClient.Builder()
        .dispatcher(new Dispatcher(
            httpTracing.tracing().currentTraceContext()
                .executorService(new Dispatcher().executorService())))
        .addNetworkInterceptor(TracingInterceptor.create(httpTracing))
        .build();
  }

}
