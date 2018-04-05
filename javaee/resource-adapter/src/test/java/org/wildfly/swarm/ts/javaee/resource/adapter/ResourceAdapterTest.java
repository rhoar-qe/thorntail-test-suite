package org.wildfly.swarm.ts.javaee.resource.adapter;

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
public class ResourceAdapterTest {
    @Test
    @RunAsClient
    public void testResourceAdapter() throws IOException {
        String response = Request.Get("http://localhost:8080/ra").execute().returnContent().asString();
        assertThat(response).isEqualTo("OK");
    }
}
