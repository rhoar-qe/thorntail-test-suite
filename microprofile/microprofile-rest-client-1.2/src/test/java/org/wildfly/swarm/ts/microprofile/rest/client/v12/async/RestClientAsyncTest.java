package org.wildfly.swarm.ts.microprofile.rest.client.v12.async;

import org.apache.http.client.fluent.Request;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ENTSWM-554
 */
@RunWith(Arquillian.class)
@DefaultDeployment
public class RestClientAsyncTest {
    @Test
    @RunAsClient
    public void directCall() throws IOException {
        String response = Request.Get("http://localhost:8080/rest/simple/hello/Alice").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello 'Alice' from async");
    }

    @Test
    @RunAsClient
    public void restClientCall() throws IOException {
        String response = Request.Get("http://localhost:8080/rest/client/hello").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello 'Bob' from async");
    }
}
