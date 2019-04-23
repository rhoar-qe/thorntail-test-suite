package org.wildfly.swarm.ts.javaee.jaxrs.client.json.in.test;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment
public class JaxrsClientJsonUsageTest {
    @Test
    @RunAsClient
    public void test() {
        Client client = ClientBuilder.newClient();
        try {
            WebTarget target = client.target("http://localhost:8080");
            Response response = target.request().get();
            assertThat(response.getHeaderString("Content-Type")).contains("application/json");
            Hello hello = response.readEntity(Hello.class);
            assertThat(hello.value).isEqualTo("Hello in JSON");
        } finally {
            client.close();
        }
    }
}
