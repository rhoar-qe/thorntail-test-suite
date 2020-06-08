package org.wildfly.swarm.ts.microprofile.rest.client.v14;

import java.io.IOException;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.http.client.fluent.Request;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment
public class HeadersViaFactoryTest {

    @Test
    @RunAsClient
    public void checkHeaders() throws IOException {
        String response = Request.Get("http://localhost:8080/rest/client").execute().returnContent().asString();
        checkJsonResponse(response);
    }

    private void checkJsonResponse(String response) {
        JsonElement json = JsonParser.parseString(response);
        assertThat(json.isJsonObject()).isTrue();
        assertThat(json.getAsJsonObject().has("FOO")).isTrue();
        assertThat(json.getAsJsonObject().get("FOO").getAsString()).isEqualTo("BAR");

        assertThat(json.getAsJsonObject().has("INJECTED_COUNT")).isTrue();
        assertThat(json.getAsJsonObject().get("INJECTED_COUNT").getAsString()).isEqualTo("1").as("Counter is injected to ClientHeadersFactory implementation");
    }
}