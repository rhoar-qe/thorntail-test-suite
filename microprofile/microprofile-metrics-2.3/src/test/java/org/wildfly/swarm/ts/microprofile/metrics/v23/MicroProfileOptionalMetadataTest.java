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
public class MicroProfileOptionalMetadataTest {
    @Test
    @RunAsClient
    @InSequence(1)
    public void trigger() throws IOException {
        String response = Request.Get("http://localhost:8080/optional-metadata").execute().returnContent().asString();
        assertThat(response).isEqualTo("optional metadata");
    }

    @Test
    @RunAsClient
    @InSequence(2)
    public void jsonMetadata() throws IOException {
        String response = Request.Options("http://localhost:8080/metrics")
                .addHeader("Accept", "application/json").execute().returnContent().asString();
        JsonObject json = JsonParser.parseString(response).getAsJsonObject();
        assertThat(json.has("application")).isTrue();
        JsonObject app = json.getAsJsonObject("application");
        assertThat(app.has("counter")).isTrue();
        JsonObject counter = app.getAsJsonObject("counter");
        assertThat(counter.get("displayName").getAsString()).isEqualTo("counter");
        assertThat(counter.has("description")).isFalse();
    }

    @Test
    @RunAsClient
    @InSequence(3)
    public void jsonData() throws IOException {
        String response = Request.Get("http://localhost:8080/metrics")
                .addHeader("Accept", "application/json").execute().returnContent().asString();
        JsonObject json = JsonParser.parseString(response).getAsJsonObject();
        assertThat(json.has("application")).isTrue();
        JsonObject app = json.getAsJsonObject("application");
        assertThat(app.has("counter")).isTrue();
        assertThat(app.get("counter").getAsInt()).isEqualTo(1);
    }
}
