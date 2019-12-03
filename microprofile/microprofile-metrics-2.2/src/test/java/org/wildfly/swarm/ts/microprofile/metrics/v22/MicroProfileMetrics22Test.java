package org.wildfly.swarm.ts.microprofile.metrics.v22;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.client.fluent.Request;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment
public class MicroProfileMetrics22Test {
    // tested features new in MicroProfile Metrics 2.2.1, compared to 2.0:
    // - MetadataBuilder.reusable(boolean)
    // - new metric `cpu.processCpuTime`

    @Test
    @RunAsClient
    @InSequence(1)
    public void trigger() throws IOException {
        String response = Request.Get("http://localhost:8080/").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello from programmatically counted method");

        for (int i = 0; i < 10; i++) {
            Request.Get("http://localhost:8080/").execute().discardContent();
        }
    }

    @Test
    @RunAsClient
    @InSequence(2)
    public void jsonMetadata() throws IOException {
        String response = Request.Options("http://localhost:8080/metrics")
                .addHeader("Accept", "application/json").execute().returnContent().asString();
        JsonObject json = new JsonParser().parse(response).getAsJsonObject();
        assertThat(json.has("application")).isTrue();
        JsonObject app = json.getAsJsonObject("application");
        assertThat(app.has("hello-count")).isTrue();
        assertThat(app.getAsJsonObject("hello-count").get("displayName").getAsString()).isEqualTo("Hello Count");
        assertThat(app.getAsJsonObject("hello-count").has("description")).isTrue();
        assertThat(app.getAsJsonObject("hello-count").get("description").getAsString()).isEqualTo("Number of hello invocations");
        assertThat(app.getAsJsonObject("hello-count").get("unit").getAsString()).isEqualTo("none");
    }

    @Test
    @RunAsClient
    @InSequence(3)
    public void jsonData() throws IOException {
        String response = Request.Get("http://localhost:8080/metrics")
                .addHeader("Accept", "application/json").execute().returnContent().asString();
        JsonObject json = new JsonParser().parse(response).getAsJsonObject();
        assertThat(json.has("application")).isTrue();
        JsonObject app = json.getAsJsonObject("application");
        assertThat(app.has("hello-count")).isTrue();
        assertThat(app.get("hello-count").getAsInt()).isEqualTo(11);
    }

    @Test
    @RunAsClient
    @InSequence(4)
    public void newMetrics() throws IOException {
        String response = Request.Get("http://localhost:8080/metrics")
                .addHeader("Accept", "application/json").execute().returnContent().asString();
        JsonObject json = new JsonParser().parse(response).getAsJsonObject();
        assertThat(json.has("base")).isTrue();
        JsonObject app = json.getAsJsonObject("base");
        assertThat(app.has("cpu.processCpuTime")).isTrue();
        assertThat(app.has("cpu.processCpuLoad")).isTrue();
        assertThat(app.has("memory.committedHeap")).isTrue();
        assertThat(app.has("memory.committedNonHeap")).isTrue();
        assertThat(app.has("memory.maxHeap")).isTrue();
        assertThat(app.has("memory.maxNonHeap")).isTrue();
        assertThat(app.has("memory.usedHeap")).isTrue();
    }
}
