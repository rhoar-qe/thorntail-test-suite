package org.wildfly.swarm.ts.microprofile.rest.client.v13;

import java.io.IOException;

import org.apache.http.client.fluent.Request;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment
public class MicroprofileRestClient13Test {

    @Test
    @RunAsClient
    public void configKey() throws IOException {
        String response = Request.Get("http://localhost:8080/rest/client/config-key").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello from endpoint");
    }

    @Test
    @RunAsClient
    public void missingConfigKey() throws IOException {
        String response = Request.Get("http://localhost:8080/rest/client/missing-config-key").execute().returnContent().asString();
        assertThat(response).contains("Neither baseUri nor baseUrl was specified");
    }

    @Test
    @RunAsClient
    public void closingOfClient() throws IOException {
        String response = Request.Get("http://localhost:8080/rest/client/manual-close").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello from endpoint, client was closed as expected.");

    }

    @Test
    @RunAsClient
    public void autoClosingOfClient() throws IOException {
        String response = Request.Get("http://localhost:8080/rest/client/auto-close").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello from endpoint, client was auto-closed as expected.");
    }
}
