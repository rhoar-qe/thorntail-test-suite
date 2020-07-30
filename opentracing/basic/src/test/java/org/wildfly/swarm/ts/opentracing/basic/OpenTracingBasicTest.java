package org.wildfly.swarm.ts.opentracing.basic;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.awaitility.Awaitility.await;

@RunWith(Arquillian.class)
@DefaultDeployment
public class OpenTracingBasicTest {
    @ClassRule
    public static Docker jaegerContainer = DockerContainers.jaeger();

    @Test
    @InSequence(1)
    @RunAsClient
    public void applicationRequest() throws IOException {
        String response = Request.Get("http://localhost:8080/").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello from traced servlet");
    }

    @Test
    @InSequence(2)
    @RunAsClient
    public void trace() {
        // the tracer inside the application doesn't send traces to the Jaeger server immediately,
        // they are batched, so we need to wait a bit
        await().atMost(20, TimeUnit.SECONDS).untilAsserted(() -> {
            String response = Request.Get("http://localhost:16686/api/traces?service=test-traced-service").execute().returnContent().asString();
            JsonObject json = JsonParser.parseString(response).getAsJsonObject();
            assertThat(json.has("data")).isTrue();
            JsonArray data = json.getAsJsonArray("data");
            assertThat(data.size()).isEqualTo(1);
            JsonObject trace = data.get(0).getAsJsonObject();
            assertThat(trace.has("spans")).isTrue();
            JsonArray spans = trace.getAsJsonArray("spans");
            assertThat(spans).hasSize(1);
            JsonObject span = spans.get(0).getAsJsonObject();
            assertThat(span.has("tags")).isTrue();
            JsonArray tags = span.getAsJsonArray("tags");
            for (JsonElement tagElement : tags) {
                JsonObject tag = tagElement.getAsJsonObject();
                switch (tag.get("key").getAsString()) {
                    case "http.method":
                        assertThat(tag.get("value").getAsString()).isEqualTo("GET");
                        break;
                    case "http.url":
                        assertThat(tag.get("value").getAsString()).isEqualTo("http://localhost:8080/");
                        break;
                    case "http.status_code":
                        assertThat(tag.get("value").getAsInt()).isEqualTo(200);
                        break;
                    case "component":
                    case "span.kind":
                    case "sampler.type":
                    case "sampler.param":
                        // do nothing
                        break;
                    default:
                        fail(this.getClass().getSimpleName() + " unknown key: " + tag.get("key").getAsString());
                }
            }
        });
    }
}
