package org.wildfly.swarm.ts.javaee.staticcontent;

import org.apache.http.client.fluent.Request;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class StaticContentIT {
    @Test
    public void staticContent() throws IOException {
        String response = Request.Get("http://localhost:8080/").execute().returnContent().asString();
        assertThat(response).contains("Hello from static HTML page");
        assertThat(response).contains("assets/style.css");

        response = Request.Get("http://localhost:8080/index.html").execute().returnContent().asString();
        assertThat(response).contains("Hello from static HTML page");
        assertThat(response).contains("assets/style.css");

        response = Request.Get("http://localhost:8080/assets/style.css").execute().returnContent().asString();
        assertThat(response).contains("color: red");
    }

    @Test
    public void jaxrs() throws IOException {
        String response = Request.Get("http://localhost:8080/api/hello").execute().returnContent().asString();
        assertThat(response).contains("Hello from JAX-RS");
    }
}
