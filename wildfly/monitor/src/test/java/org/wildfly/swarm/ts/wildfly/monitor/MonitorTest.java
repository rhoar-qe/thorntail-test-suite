package org.wildfly.swarm.ts.wildfly.monitor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.http.client.fluent.Request;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment
public class MonitorTest {
    @Test
    @RunAsClient
    public void hello() throws IOException {
        String response = Request.Get("http://localhost:8080/").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello");
    }

    @Test
    @RunAsClient
    public void myHealthCheck() throws IOException {
        String response = Request.Get("http://localhost:8080/my-health-check").execute().returnContent().asString();
        JsonElement json = new JsonParser().parse(response);
        assertThat(json.isJsonObject()).isTrue();
        assertThat(json.getAsJsonObject().get("id").getAsString()).isEqualTo("hello");
        assertThat(json.getAsJsonObject().get("result").getAsString()).isEqualTo("UP");
    }

    @Test
    @RunAsClient
    public void health() throws IOException {
        String response = Request.Get("http://localhost:8080/health").execute().returnContent().asString();
        JsonElement json = new JsonParser().parse(response);
        assertThat(json.isJsonObject()).isTrue();
        assertThat(json.getAsJsonObject().get("outcome").getAsString()).isEqualTo("UP");
        JsonArray checks = json.getAsJsonObject().getAsJsonArray("checks");
        assertThat(checks.size()).isEqualTo(1);
        JsonElement check = checks.get(0);
        assertThat(check.isJsonObject()).isTrue();
        assertThat(check.getAsJsonObject().get("id").getAsString()).isEqualTo("hello");
        assertThat(check.getAsJsonObject().get("result").getAsString()).isEqualTo("UP");
    }

    @Test
    @RunAsClient
    public void node() throws IOException {
        String response = Request.Get("http://localhost:8080/node").execute().returnContent().asString();
        JsonElement json = new JsonParser().parse(response);
        assertThat(json.isJsonObject()).isTrue();
        assertThat(json.getAsJsonObject().has("name")).isTrue();
        assertThat(json.getAsJsonObject().has("server-state")).isTrue();
        assertThat(json.getAsJsonObject().has("suspend-state")).isTrue();
        assertThat(json.getAsJsonObject().has("running-mode")).isTrue();
        assertThat(json.getAsJsonObject().has("swarm-version")).isTrue();
    }

    @Test
    @RunAsClient
    public void heap() throws IOException {
        String response = Request.Get("http://localhost:8080/heap").execute().returnContent().asString();
        JsonElement json = new JsonParser().parse(response);
        assertThat(json.isJsonObject()).isTrue();
        assertThat(json.getAsJsonObject().has("heap-memory-usage")).isTrue();
        assertThat(json.getAsJsonObject().getAsJsonObject("heap-memory-usage").has("init")).isTrue();
        assertThat(json.getAsJsonObject().getAsJsonObject("heap-memory-usage").has("used")).isTrue();
        assertThat(json.getAsJsonObject().getAsJsonObject("heap-memory-usage").has("committed")).isTrue();
        assertThat(json.getAsJsonObject().getAsJsonObject("heap-memory-usage").has("max")).isTrue();
        assertThat(json.getAsJsonObject().has("non-heap-memory-usage")).isTrue();
        assertThat(json.getAsJsonObject().getAsJsonObject("non-heap-memory-usage").has("init")).isTrue();
        assertThat(json.getAsJsonObject().getAsJsonObject("non-heap-memory-usage").has("used")).isTrue();
        assertThat(json.getAsJsonObject().getAsJsonObject("non-heap-memory-usage").has("committed")).isTrue();
        assertThat(json.getAsJsonObject().getAsJsonObject("non-heap-memory-usage").has("max")).isTrue();
    }

    @Test
    @RunAsClient
    public void threads() throws IOException {
        String response = Request.Get("http://localhost:8080/threads").execute().returnContent().asString();
        JsonElement json = new JsonParser().parse(response);
        assertThat(json.isJsonObject()).isTrue();
        assertThat(json.getAsJsonObject().has("thread-count")).isTrue();
        assertThat(json.getAsJsonObject().has("peak-thread-count")).isTrue();
        assertThat(json.getAsJsonObject().has("total-started-thread-count")).isTrue();
        assertThat(json.getAsJsonObject().has("current-thread-cpu-time")).isTrue();
        assertThat(json.getAsJsonObject().has("current-thread-user-time")).isTrue();
    }
}
