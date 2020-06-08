package org.wildfly.swarm.ts.microprofile.metrics.v23;

import java.io.IOException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.client.fluent.Request;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment
public class MicroProfileSimpleTimerTest {

    @Test
    @RunAsClient
    @InSequence(1)
    public void trigger() throws IOException {
        String response = Request.Get("http://localhost:8080/simple-timer").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello from timed method");
    }

    @Test
    @RunAsClient
    @InSequence(2)
    public void timerData() throws IOException {
        String response = Request.Get("http://localhost:8080/metrics")
                .addHeader("Accept", "application/json").execute().returnContent().asString();
        JsonObject json = JsonParser.parseString(response).getAsJsonObject();
        assertThat(json.has("application")).isTrue();
        JsonObject app = json.getAsJsonObject("application");
        assertThat(app.has("simple-timer")).isTrue();
        JsonObject simpleTimer = app.getAsJsonObject("simple-timer");
        assertThat(simpleTimer.has("count")).isTrue();
        assertThat(simpleTimer.get("count").getAsInt()).isEqualTo(1);
        assertThat(simpleTimer.has("elapsedTime")).isTrue();
        assertThat(simpleTimer.get("elapsedTime").getAsDouble()).isGreaterThanOrEqualTo(10.0);
    }
}
