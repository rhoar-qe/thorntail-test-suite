package org.wildfly.swarm.ts.wildfly.management;

import org.apache.http.client.fluent.Request;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.IOException;
import java.io.StringReader;

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
        JsonObject json = Json.createReader(new StringReader(response)).readObject();
        assertThat(json).containsKey("core-service");
        assertThat(json).containsKey("deployment");
        assertThat(json).containsKey("extension");
        assertThat(json).containsKey("interface");
        assertThat(json).containsKey("subsystem");
    }
}
