package org.wildfly.swarm.ts.netflix.hystrix;

import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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
    public void hystrix() throws Exception {
        String response = Request.Get("http://localhost:8080/").execute().returnContent().toString();
        assertThat(response).isEqualTo("OK");

        Request.Post("http://localhost:8080/throwError").execute().discardContent();

        response = Request.Get("http://localhost:8080/").execute().returnContent().toString();
        assertThat(response).isEqualTo("Fallback string");
    }

    @Test
    @RunAsClient
    public void hystrixStream() throws Exception {
        // can't use the fluent-hc API, because it consumes the entire response body, which is infinite in this case
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet httpget = new HttpGet("http://localhost:8080/hystrix.stream");
            try (CloseableHttpResponse response = client.execute(httpget)) {
                assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
            }
        }
    }
}
