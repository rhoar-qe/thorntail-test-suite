package org.wildfly.swarm.ts.wildfly.management;

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
public class ManagementTest {
    @Test
    @RunAsClient
    public void hello() throws IOException {
        String response = Request.Get("http://localhost:8080/").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello");
    }

    @Test
    @RunAsClient
    public void management() throws IOException {
        String response = Request.Get("http://localhost:9990/management").execute().returnContent().asString();
        JsonElement json = new JsonParser().parse(response);
        assertThat(json.isJsonObject()).isTrue();
        assertThat(json.getAsJsonObject().has("core-service"));
        assertThat(json.getAsJsonObject().has("deployment"));
        assertThat(json.getAsJsonObject().has("extension"));
        assertThat(json.getAsJsonObject().has("interface"));
        assertThat(json.getAsJsonObject().has("subsystem"));
    }
}
