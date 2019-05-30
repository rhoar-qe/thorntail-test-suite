package org.wildfly.swarm.ts.microprofile.opentracing.v13;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.client.fluent.Request;
import org.assertj.core.api.Assertions;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;
import org.wildfly.swarm.ts.common.docker.Docker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@RunWith(Arquillian.class)
@DefaultDeployment
public class MPOpenTracing13Test {
    private static final String PREFIX = "org.wildfly.swarm.ts.microprofile.opentracing.v13.";

    private static final String GET_PREFIX = "GET:" + PREFIX;

    private static final String URL_REST = "http://localhost:8080/rest/";

    private ImmutableMap<String, String> URL_TO_RESPONSES = ImmutableMap.<String, String>builder()
            .put("tracedlogged", "Hello from tracedLoggedMethod")
            .put("traced", "Hello from tracedMethod")
            .put("logged", "Hello from loggedMethod")
            .put("nottracednotlogged", "Hello from notTracedNotLoggedMethod")

            .put("named/true", "Hello from tracedNamedTrue")
            .put("true", "Hello from tracedTrue")
            .put("named/false", "Hello from tracedNamedFalse")
            .put("false", "Hello from tracedFalse")
            // test http-path when path contains regular expressions (#136)
            .put("test/1/hello", "Hello from twoWildcard: 1, hello")
            .build();

    private List<Trace> EXPECTED_TRACES = Arrays.asList(
            // traced and tracer.span.logging combinations
            new Trace("Resource.tracedLoggedMethod", null, PREFIX + "TracedService.tracedLoggedMethod", "tracer: tracedLoggedMethod"),
            new Trace("Resource.tracedMethod", null, PREFIX + "TracedService.tracedMethod", null),
            new Trace("Resource.loggedMethod", "tracer: loggedMethod"),
            new Trace("Resource.notTracedNotLoggedMethod"),
            // operationName with on/off combination
            new Trace("Resource.tracedNamedTrue", null, "operationName-should-appear-tracedNamedTrue", "tracedNamedTrue"),
            new Trace("Resource.tracedTrue", null, PREFIX + "TracedService.tracedTrue", "tracedTrue"),
            new Trace("Resource.tracedNamedFalse", "tracedNamedFalse"),
            new Trace("Resource.tracedFalse", "tracedFalse"),
            // two wild cards
            new Trace("Resource.twoWildcard", null, PREFIX + "TracedService.twoWildcard", "twoWildcard: 1, hello")
    );

    @ClassRule
    public static Docker jaegerContainer = new Docker("jaeger", "jaegertracing/all-in-one:latest")
            .waitForLogLine("\"Health Check state change\",\"status\":\"ready\"")
            // https://www.jaegertracing.io/docs/1.11/getting-started/
            .port("5775:5775/udp")
            .port("6831:6831/udp")
            .port("6832:6832/udp")
            .port("5778:5778")
            .port("16686:16686")
            .port("14250:14250")
            .port("14267:14267")
            .port("14268:14268")
            .port("9411:9411")
            .envVar("COLLECTOR_ZIPKIN_HTTP_PORT", "9411")
            .cmdArg("--reporter.grpc.host-port=localhost:14250");

    @Test
    @InSequence(1)
    @RunAsClient
    public void applicationRequest() {
        URL_TO_RESPONSES.forEach((url, expectedResponse) -> {
            try {
                assertThat(Request.Get(URL_REST + url).execute().returnContent().asString())
                        .isEqualTo(expectedResponse);
            } catch (IOException e) {
                Assertions.fail("IOException occurred for url: " + URL_REST + url +
                                        ", when expecting: \"" + expectedResponse + "\": " + e.getMessage());
            }
        });
    }

    @Test
    @InSequence(2)
    @RunAsClient
    public void trace() {
        // the tracer inside the application doesn't send traces to the Jaeger server immediately,
        // they are batched, so we need to wait a bit
        await().atMost(20, TimeUnit.SECONDS).untilAsserted(() -> {
            String response = Request.Get("http://localhost:16686/api/traces?service=test-traced-service").execute().returnContent().asString();
            JsonObject json = new JsonParser().parse(response).getAsJsonObject();
            assertThat(json.has("data")).isTrue();
            JsonArray data = json.getAsJsonArray("data");

            assertThat(data.size()).isEqualTo(EXPECTED_TRACES.size());

            EXPECTED_TRACES.forEach(trace -> checkTrace(data,
                                                        trace.getOperationName(),
                                                        trace.getLog(),
                                                        trace.getChildOperationName(),
                                                        trace.getChildLog()));
        });
    }

    private static void checkTrace(final JsonArray data, final String operationName, final String log,
                                   final String childOperationName, final String childLog) {
        assertThat(data).anySatisfy(element -> {
            JsonObject elemObj = element.getAsJsonObject();
            assertThat(elemObj.has("spans")).isTrue();
            JsonArray spans = elemObj.getAsJsonArray("spans");
            if (spans.size() == 1) {
                JsonObject span = spans.get(0).getAsJsonObject();
                // check operation name match
                assertThat(span.get("operationName").getAsString()).isEqualTo(operationName);
                // check log is empty or has desired value from tracer
                checkLogPart(log, span);
            } else if (spans.size() == 2) {
                if (childOperationName != null) {
                    JsonObject span1 = spans.get(0).getAsJsonObject();
                    JsonObject span2 = spans.get(1).getAsJsonObject();
                    if (operationName.equals(span1.get("operationName").getAsString())) {
                        checkLogPart(log, span1);
                        // check child
                        assertThat(childOperationName).isEqualTo(span2.get("operationName").getAsString());
                        checkLogPart(childLog, span2);
                    } else if (operationName.equals(span2.get("operationName").getAsString())) {
                        checkLogPart(log, span2);
                        // check child
                        assertThat(childOperationName).isEqualTo(span1.get("operationName").getAsString());
                        checkLogPart(childLog, span1);
                    } else {
                        Assertions.fail("Unable to find parent span with name: " + operationName);
                    }
                }
            }
        });
    }

    private static void checkLogPart(String log, JsonObject span) {
        JsonArray logArray = span.get("logs").getAsJsonArray();
        // there should be only 0 or 1 log included
        if (log == null) {
            assertThat(logArray.size()).isEqualTo(0);
        } else {
            assertThat(logArray.size()).isEqualTo(1);
            String logValue = logArray.get(0).getAsJsonObject().getAsJsonArray("fields").get(0).getAsJsonObject().get("value").getAsString();
            assertThat(logValue).isEqualTo(log);
        }
    }

    private class Trace {
        private String operationName;

        private String log;

        private String childOperationName;

        private String childLog;

        Trace(String operationName) {
            new Trace(operationName, null);
        }

        Trace(String operationName, String log) {
            new Trace(operationName, log, null, null);
        }

        Trace(String operationName, String log, String childOperationName, String childLog) {
            this.operationName = GET_PREFIX + operationName;
            this.log = log;
            this.childOperationName = childOperationName;
            this.childLog = childLog;

        }

        String getOperationName() {
            return operationName;
        }

        String getLog() {
            return log;
        }

        String getChildOperationName() {
            return childOperationName;
        }

        String getChildLog() {
            return childLog;
        }
    }
}