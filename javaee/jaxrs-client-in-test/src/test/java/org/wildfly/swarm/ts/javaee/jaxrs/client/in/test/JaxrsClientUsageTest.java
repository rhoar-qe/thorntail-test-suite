package org.wildfly.swarm.ts.javaee.jaxrs.client.in.test;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment
public class JaxrsClientUsageTest {
    @Test
    @RunAsClient
    public void test() throws IOException {
        Client client = ClientBuilder.newClient();
        try {
            WebTarget target = client.target("http://localhost:8080");
            Response response = target.request().get();
            assertThat(response.readEntity(String.class)).isEqualTo("Hello from testing servlet with JAX-RS Client");
        } finally {
            client.close();
        }
    }
}
