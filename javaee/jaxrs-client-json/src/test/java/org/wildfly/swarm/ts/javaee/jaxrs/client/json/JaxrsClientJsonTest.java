package org.wildfly.swarm.ts.javaee.jaxrs.client.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment
public class JaxrsClientJsonTest {
    @Test
    @RunAsClient
    public void test() throws IOException {
        HttpResponse response = Request.Get("http://localhost:8080/client").execute().returnResponse();
        assertThat(response.getFirstHeader("Content-Type").getValue()).contains("application/json");

        JsonElement json = new JsonParser().parse(EntityUtils.toString(response.getEntity()));
        assertThat(json.isJsonObject()).isTrue();
        assertThat(json.getAsJsonObject().has("value")).isTrue();
        assertThat(json.getAsJsonObject().get("value").getAsString()).isEqualTo("JAX-RS Client got: Hello in JSON");
    }
}
