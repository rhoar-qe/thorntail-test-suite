package org.wildfly.swarm.ts.wildfly.monitor;

import org.apache.http.client.fluent.Request;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.IOException;
import java.io.StringReader;

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
        JsonObject json = Json.createReader(new StringReader(response)).readObject();
        assertThat(json.getString("id")).isEqualTo("hello");
        assertThat(json.getString("result")).isEqualTo("UP");
    }

    @Test
    @RunAsClient
    public void health() throws IOException {
        String response = Request.Get("http://localhost:8080/health").execute().returnContent().asString();
        JsonObject json = Json.createReader(new StringReader(response)).readObject();
        assertThat(json.getString("outcome")).isEqualTo("UP");
        JsonArray checks = json.getJsonArray("checks");
        assertThat(checks).hasSize(1);
        JsonObject check = checks.getJsonObject(0);
        assertThat(check.getString("id")).isEqualTo("hello");
        assertThat(check.getString("result")).isEqualTo("UP");
    }

    @Test
    @RunAsClient
    public void node() throws IOException {
        String response = Request.Get("http://localhost:8080/node").execute().returnContent().asString();
        JsonObject json = Json.createReader(new StringReader(response)).readObject();
        assertThat(json).containsKey("name");
        assertThat(json).containsKey("server-state");
        assertThat(json).containsKey("suspend-state");
        assertThat(json).containsKey("running-mode");
        assertThat(json).containsKey("swarm-version");
    }

    @Test
    @RunAsClient
    public void heap() throws IOException {
        String response = Request.Get("http://localhost:8080/heap").execute().returnContent().asString();
        JsonObject json = Json.createReader(new StringReader(response)).readObject();
        assertThat(json).containsKey("heap-memory-usage");
        assertThat(json.getJsonObject("heap-memory-usage")).containsKey("init");
        assertThat(json.getJsonObject("heap-memory-usage")).containsKey("used");
        assertThat(json.getJsonObject("heap-memory-usage")).containsKey("committed");
        assertThat(json.getJsonObject("heap-memory-usage")).containsKey("max");
        assertThat(json).containsKey("non-heap-memory-usage");
        assertThat(json.getJsonObject("non-heap-memory-usage")).containsKey("init");
        assertThat(json.getJsonObject("non-heap-memory-usage")).containsKey("used");
        assertThat(json.getJsonObject("non-heap-memory-usage")).containsKey("committed");
        assertThat(json.getJsonObject("non-heap-memory-usage")).containsKey("max");
    }

    @Test
    @RunAsClient
    public void threads() throws IOException {
        String response = Request.Get("http://localhost:8080/threads").execute().returnContent().asString();
        JsonObject json = Json.createReader(new StringReader(response)).readObject();
        assertThat(json).containsKey("thread-count");
        assertThat(json).containsKey("peak-thread-count");
        assertThat(json).containsKey("total-started-thread-count");
        assertThat(json).containsKey("current-thread-cpu-time");
        assertThat(json).containsKey("current-thread-user-time");
    }
}
