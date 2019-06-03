package org.wildfly.swarm.ts.microprofile.health.check.v20;

import java.io.IOException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.client.fluent.Request;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment
public class MicroProfileHealthCheck20Test {
    private static final String URL_HEALTH = "http://localhost:8080/health";

    private static final String URL_HEALTH_LIVE = URL_HEALTH + "/live";

    private static final String URL_HEALTH_READY = URL_HEALTH + "/ready";

    @Test
    @RunAsClient
    public void test() throws IOException {
        String response = Request.Get(URL_HEALTH).execute().returnContent().asString();
        // expected check count is 5 because BothHealthCheck contains both annotations: @Liveness and @Readiness
        checkResponse(response, 5, "both", "deprecated-health", "live", "ready");

        response = Request.Get(URL_HEALTH_LIVE).execute().returnContent().asString();
        checkResponse(response, 2, "both", "live");

        response = Request.Get(URL_HEALTH_READY).execute().returnContent().asString();
        checkResponse(response, 2, "both", "ready");
    }

    private void checkResponse(String response, int checkCount, String... names) {
        JsonElement json = new JsonParser().parse(response);
        assertThat(json.isJsonObject()).isTrue();
        assertThat(json.getAsJsonObject().has("status")).isTrue();
        assertThat(json.getAsJsonObject().get("status").getAsString()).isEqualTo("UP");
        assertThat(json.getAsJsonObject().has("checks")).isTrue();
        JsonArray checks = json.getAsJsonObject().getAsJsonArray("checks");
        assertThat(checks.size()).isEqualTo(checkCount);
        for (JsonElement checkElement : checks.getAsJsonArray()) {
            checkCheckElement(checkElement, names);
        }
    }

    private void checkCheckElement(JsonElement checkElement, String... expectedNames) {
        JsonObject check = checkElement.getAsJsonObject();
        assertThat(check.has("name")).isTrue();

        String actualName = check.getAsJsonObject().get("name").getAsString();
        assertThat(expectedNames).contains(actualName);
        assertThat(check.has("status")).isTrue();
        assertThat(check.get("status").getAsString()).isEqualTo("UP");
        assertThat(check.has("data")).isTrue();
        assertThat(check.get("data").getAsJsonObject().get("key").getAsString()).isEqualTo("value");
    }
}
