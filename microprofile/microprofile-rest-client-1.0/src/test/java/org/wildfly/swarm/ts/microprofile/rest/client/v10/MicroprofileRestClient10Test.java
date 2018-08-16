package org.wildfly.swarm.ts.microprofile.rest.client.v10;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.apache.http.client.fluent.Request;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

@RunWith(Arquillian.class)
@DefaultDeployment
public class MicroprofileRestClient10Test {
    @Test
    @InSequence(1)
    @RunAsClient
    public void applicationRequest() throws IOException {
        String response = Request.Get("http://localhost:8080/rest/simple").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello from endpoint");
    }

    @Test
    @InSequence(2)
    @RunAsClient
    public void injectedRestClient() throws IOException {
        String response = Request.Get("http://localhost:8080/rest/client/injected").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello from endpoint");
    }

    @Test
    @InSequence(3)
    @RunAsClient
    public void programaticRestClient() throws IOException {
        String response = Request.Get("http://localhost:8080/rest/client/programatic").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello from endpoint");
    }
}
