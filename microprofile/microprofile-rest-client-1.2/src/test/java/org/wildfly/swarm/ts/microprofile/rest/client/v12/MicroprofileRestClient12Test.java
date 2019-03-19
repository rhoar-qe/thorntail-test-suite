package org.wildfly.swarm.ts.microprofile.rest.client.v12;

import org.apache.http.client.fluent.Request;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment
public class MicroprofileRestClient12Test {
    @Test
    @InSequence(1)
    @RunAsClient
    public void applicationRequest() throws IOException {
        String response = Request.Get("http://localhost:8080/rest/simple1").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello from endpoint1");

        response = Request.Get("http://localhost:8080/rest/simple2").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello from endpoint2");

        response = Request.Get("http://localhost:8080/rest/simple3").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello from endpoint3");
    }

    @Test
    @InSequence(2)
    @RunAsClient
    public void injectedRestClient() throws IOException {
        String response = Request.Get("http://localhost:8080/rest/client/injected1").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello from endpoint1");

        response = Request.Get("http://localhost:8080/rest/client/injected2").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello from endpoint2");

        response = Request.Get("http://localhost:8080/rest/client/injected3").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello from endpoint3");
    }

    @Test
    @InSequence(3)
    @RunAsClient
    public void programaticRestClient() throws IOException {
        String response = Request.Get("http://localhost:8080/rest/client/programatic1").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello from endpoint1");

        response = Request.Get("http://localhost:8080/rest/client/programatic2").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello from endpoint2");

        response = Request.Get("http://localhost:8080/rest/client/programatic3").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello from endpoint3");
    }
}
