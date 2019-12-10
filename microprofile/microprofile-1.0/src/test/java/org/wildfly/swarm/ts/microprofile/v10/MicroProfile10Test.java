package org.wildfly.swarm.ts.microprofile.v10;

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
public class MicroProfile10Test {
    @Test
    @RunAsClient
    public void test() throws IOException {
        String response = Request.Get("http://localhost:8080/").execute().returnContent().asString();
        JsonElement json = JsonParser.parseString(response);
        assertThat(json.isJsonObject()).isTrue();
        assertThat(json.getAsJsonObject().size()).isEqualTo(1);
        assertThat(json.getAsJsonObject().has("content")).isTrue();
        assertThat(json.getAsJsonObject().get("content").getAsString()).isEqualTo("Hello, World!");
    }
}
