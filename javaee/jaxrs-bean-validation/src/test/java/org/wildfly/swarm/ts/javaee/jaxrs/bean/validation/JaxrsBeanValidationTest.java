package org.wildfly.swarm.ts.javaee.jaxrs.bean.validation;

import org.apache.http.HttpResponse;
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
public class JaxrsBeanValidationTest {
    @Test
    @RunAsClient
    public void clientError_paramTooLong() throws IOException {
        HttpResponse response = Request.Get("http://localhost:8080/?param=12345678901234567890").execute().returnResponse();
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(400);
    }

    @Test
    @RunAsClient
    public void serverError_returnValueTooShort() throws IOException {
        HttpResponse response = Request.Get("http://localhost:8080/?param=123").execute().returnResponse();
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(500);
    }

    @Test
    @RunAsClient
    public void ok() throws IOException {
        String response = Request.Get("http://localhost:8080/?param=12345").execute().returnContent().asString();
        assertThat(response).isEqualTo("12345 12345");
    }
}
