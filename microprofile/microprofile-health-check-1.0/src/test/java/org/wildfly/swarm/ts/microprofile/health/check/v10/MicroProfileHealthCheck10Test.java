package org.wildfly.swarm.ts.microprofile.health.check.v10;

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

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment
public class MicroProfileHealthCheck10Test {
    @Test
    @RunAsClient
    public void test() throws IOException {
        String response = Request.Get("http://localhost:8080/health").execute().returnContent().asString();
        JsonElement json = new JsonParser().parse(response);
        assertThat(json.isJsonObject()).isTrue();
        assertThat(json.getAsJsonObject().has("outcome")).isTrue();
        assertThat(json.getAsJsonObject().get("outcome").getAsString()).isEqualTo("UP");
        assertThat(json.getAsJsonObject().has("checks")).isTrue();
        JsonArray checks = json.getAsJsonObject().getAsJsonArray("checks");
        assertThat(checks.size()).isEqualTo(1);
        JsonObject check = checks.get(0).getAsJsonObject();
        assertThat(check.has("name")).isTrue();
        assertThat(check.get("name").getAsString()).isEqualTo("elvis-lives");
        assertThat(check.has("state")).isTrue();
        assertThat(check.get("state").getAsString()).isEqualTo("UP");
        assertThat(check.has("data")).isTrue();
        assertThat(check.get("data").getAsJsonObject().get("it's").getAsString()).isEqualTo("true");
    }
}
