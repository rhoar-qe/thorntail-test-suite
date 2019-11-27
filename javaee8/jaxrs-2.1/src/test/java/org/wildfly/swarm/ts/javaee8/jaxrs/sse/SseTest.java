package org.wildfly.swarm.ts.javaee8.jaxrs.sse;

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
public class SseTest {
    @Test
    @RunAsClient
    public void simpleEvent() throws IOException {
        String response = Request.Get("http://localhost:8080/client/simple").execute().returnContent().asString().trim();
        assertThat(response).isEqualTo("event: message-to-client\ndata: Hello world!");
    }

    @Test
    @RunAsClient
    public void broadcast() throws IOException {
        String response = Request.Get("http://localhost:8080/client/broadcast").execute().returnContent().asString().trim();
        assertThat(response).isEqualTo("event: Hello World\ndata: Broadcast message");
    }
}
