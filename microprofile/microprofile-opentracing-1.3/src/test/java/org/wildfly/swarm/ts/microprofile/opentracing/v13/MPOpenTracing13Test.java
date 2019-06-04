package org.wildfly.swarm.ts.microprofile.opentracing.v13;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.client.fluent.Request;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;
import org.wildfly.swarm.ts.common.docker.Docker;
import org.wildfly.swarm.ts.common.docker.DockerContainers;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.awaitility.Awaitility.await;

@RunWith(Arquillian.class)
@DefaultDeployment
public class MPOpenTracing13Test {
    private static final String PREFIX = "org.wildfly.swarm.ts.microprofile.opentracing.v13.";

    private static final String GET_PREFIX = "GET:" + PREFIX;

    private static final String URL_REST = "http://localhost:8080/rest/";

    private Map<String, String> URL_TO_RESPONSES = ImmutableMap.<String, String>builder()
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

    private Map<String, Integer> URL_EXPECTED_ERRORS = ImmutableMap.<String, Integer>builder()
            .put("traceerror", 500)
            .build();

    private List<ExpectedTrace> EXPECTED_TRACES = ImmutableList.<ExpectedTrace>builder()
            // traced and tracer.span.logging combinations
            .add(new ExpectedTrace("Resource.tracedLoggedMethod", null, null, PREFIX + "TracedService.tracedLoggedMethod", "event", "tracer: tracedLoggedMethod"))
            .add(new ExpectedTrace("Resource.tracedMethod", null, null, PREFIX + "TracedService.tracedMethod", null, null))
            .add(new ExpectedTrace("Resource.loggedMethod", "event", "tracer: loggedMethod"))
            .add(new ExpectedTrace("Resource.notTracedNotLoggedMethod"))
            // operationName with on/off combination
            .add(new ExpectedTrace("Resource.tracedNamedTrue", null, null, "operationName-should-appear-tracedNamedTrue", "event", "tracedNamedTrue"))
            .add(new ExpectedTrace("Resource.tracedTrue", null, null, PREFIX + "TracedService.tracedTrue", "event", "tracedTrue"))
            .add(new ExpectedTrace("Resource.tracedNamedFalse", "event", "tracedNamedFalse"))
            .add(new ExpectedTrace("Resource.tracedFalse", "event", "tracedFalse"))
            // two wild cards
            .add(new ExpectedTrace("Resource.twoWildcard", null, null, PREFIX + "TracedService.twoWildcard", "event", "twoWildcard: 1, hello"))

            // errors
            .add(new ExpectedTrace("Resource.traceError", "event", "error", "error", "true", PREFIX + "TracedService.traceError", "error.object", "java.lang.RuntimeException", "error", "true"))
            .build();

    @ClassRule
    public static Docker jaegerContainer = DockerContainers.jaeger();

    @Test
    @InSequence(1)
    @RunAsClient
    public void applicationRequest() {
        URL_TO_RESPONSES.forEach((url, expectedResponse) -> {
            try {
                assertThat(Request.Get(URL_REST + url).execute().returnContent().asString())
                        .isEqualTo(expectedResponse);
            } catch (IOException e) {
                fail("IOException occurred for url: " + URL_REST + url +
                                        "when expecting: \"" + expectedResponse + "\": " + e.getMessage());
            }
        });

        URL_EXPECTED_ERRORS.forEach((url, expectedResponse) -> {
            try {
                assertThat(Request.Get(URL_REST + url).execute().returnResponse().getStatusLine().getStatusCode())
                        .as("Error response was expected for url: " + URL_REST + url)
                        .isEqualTo(expectedResponse);
            } catch (IOException e) {
                fail("IOException occurred for url: " + URL_REST + url +
                        " when expecting " + expectedResponse + ": " + e.getMessage());
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

            EXPECTED_TRACES.forEach(trace -> checkTrace(data, trace));
        });
    }

    private static void checkTrace(JsonArray data, ExpectedTrace trace) {
        assertThat(data).anySatisfy(element -> {
            JsonObject traceJson = element.getAsJsonObject();
            assertThat(traceJson.has("spans")).isTrue();
            JsonArray spans = traceJson.getAsJsonArray("spans");
            if (spans.size() == 1) {
                JsonObject span = spans.get(0).getAsJsonObject();
                assertThat(span.get("operationName").getAsString()).isEqualTo(trace.operationName);
                checkSpanLog(trace.log, span);
                checkSpanTag(trace.tag, span);
            } else if (spans.size() == 2) {
                assertThat(trace.childOperationName).isNotNull();
                assertThat(spans).anySatisfy(spanElement -> {
                    JsonObject span = spanElement.getAsJsonObject();
                    assertThat(span.get("operationName").getAsString()).isEqualTo(trace.operationName);
                    checkSpanLog(trace.log, span);
                    checkSpanTag(trace.tag, span);
                });
                assertThat(spans).anySatisfy(spanElement -> {
                    JsonObject span = spanElement.getAsJsonObject();
                    assertThat(span.get("operationName").getAsString()).isEqualTo(trace.childOperationName);
                    checkSpanLog(trace.childLog, span);
                    checkSpanTag(trace.childTag, span);
                });
            } else {
                fail("Unexpected trace with " + spans.size() + " spans");
            }
        });
    }

    private static void checkSpanLog(Map.Entry<String, String> expectedLog, JsonObject span) {
        if (expectedLog == null) {
            return;
        }

        JsonObject foundLog = null;
        for (JsonElement log : span.get("logs").getAsJsonArray()) {
            for (JsonElement logFieldElement : log.getAsJsonObject().getAsJsonArray("fields")) {
                JsonObject logField = logFieldElement.getAsJsonObject();
                if (logField.get("key").getAsString().equals(expectedLog.getKey())) {
                    foundLog = logField;
                    break;
                }
            }
        }

        assertThat(foundLog).isNotNull();
        assertThat(foundLog.get("key").getAsString()).isEqualTo(expectedLog.getKey());
        assertThat(foundLog.get("value").getAsString()).isEqualTo(expectedLog.getValue());
    }

    private static void checkSpanTag(Map.Entry<String, String> expectedTag, JsonObject span) {
        if (expectedTag == null) {
            return;
        }

        JsonObject foundTag = null;
        for (JsonElement tag : span.get("tags").getAsJsonArray()) {
            if (tag.getAsJsonObject().get("key").getAsString().equals(expectedTag.getKey())) {
                foundTag = tag.getAsJsonObject();
                break;
            }
        }

        assertThat(foundTag).isNotNull();
        assertThat(foundTag.get("key").getAsString()).isEqualTo(expectedTag.getKey());
        assertThat(foundTag.get("value").getAsString()).isEqualTo(expectedTag.getValue());
    }

    private class ExpectedTrace {
        final String operationName;

        final Map.Entry<String, String> log;

        final Map.Entry<String, String> tag;

        final String childOperationName;

        final Map.Entry<String, String> childLog;

        final Map.Entry<String, String> childTag;

        ExpectedTrace(String operationName) {
            this(operationName, null, null);
        }

        ExpectedTrace(String operationName, String logKey, String logValue) {
            this(operationName, logKey, logValue, null, null, null);
        }

        ExpectedTrace(String operationName, String logKey, String logValue, String childOperationName, String childLogKey, String childLogValue) {
            this(operationName, logKey, logValue, null, null, childOperationName, childLogKey, childLogValue, null, null);
        }

        ExpectedTrace(String operationName, String logKey, String logValue, String tagKey, String tagValue,
                      String childOperationName, String childLogKey, String childLogValue, String childTagKey, String childTagValue) {
            this.operationName = GET_PREFIX + operationName;
            this.log = logKey != null ? new AbstractMap.SimpleImmutableEntry<>(logKey, logValue) : null;
            this.tag = tagKey != null ? new AbstractMap.SimpleImmutableEntry<>(tagKey, tagValue) : null;
            this.childOperationName = childOperationName;
            this.childLog = childLogKey != null ? new AbstractMap.SimpleImmutableEntry<>(childLogKey, childLogValue) : null;
            this.childTag = childTagKey != null ? new AbstractMap.SimpleImmutableEntry<>(childTagKey, childTagValue) : null;
        }
    }
}
