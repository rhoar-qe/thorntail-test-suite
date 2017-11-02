package org.wildfly.swarm.ts.netflix.hystrix;

import org.apache.http.client.fluent.Request;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment
public class HystrixTest {

    @Test
    @RunAsClient
    public void test() throws Exception {
        String response = Request.Get("http://localhost:8080/").execute().returnContent().toString();
        assertThat(response).isEqualTo("OK");

        Request.Post("http://localhost:8080/throwError").execute().discardContent();

        response = Request.Get("http://localhost:8080/").execute().returnContent().toString();
        assertThat(response).isEqualTo("Fallback string");
    }
}