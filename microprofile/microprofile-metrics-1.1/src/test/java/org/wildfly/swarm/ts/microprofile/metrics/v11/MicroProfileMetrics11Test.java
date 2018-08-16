package org.wildfly.swarm.ts.microprofile.metrics.v11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.apache.http.client.fluent.Request;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RunWith(Arquillian.class)
@DefaultDeployment
public class MicroProfileMetrics11Test {
    @Test
    @RunAsClient
    @InSequence(1)
    public void trigger() throws IOException {
        String response = Request.Get("http://localhost:8080/hello1").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello from 1st counted method");

        response = Request.Get("http://localhost:8080/hello2").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello from 2nd counted method");

        for (int i = 0; i < 10; i++) {
            Request.Get("http://localhost:8080/hello1").execute().discardContent();
            Request.Get("http://localhost:8080/hello2").execute().discardContent();
        }
    }

    @Test
    @RunAsClient
    @InSequence(2)
    public void jsonData() throws IOException {
        String response = Request.Get("http://localhost:8080/metrics")
                .addHeader("Accept", "application/json").execute().returnContent().asString();
        JsonObject json = new JsonParser().parse(response).getAsJsonObject();
        assertThat(json.has("application")).isTrue();
        JsonObject app = json.getAsJsonObject("application");
        assertThat(app.has("hello-count")).isTrue();
        assertThat(app.get("hello-count").getAsInt()).isEqualTo(22);
    }
}
