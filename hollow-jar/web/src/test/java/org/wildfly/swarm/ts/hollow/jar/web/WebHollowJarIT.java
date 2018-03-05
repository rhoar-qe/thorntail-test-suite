package org.wildfly.swarm.ts.hollow.jar.web;

import org.apache.http.client.fluent.Request;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class WebHollowJarIT {
    @Test
    public void test() throws IOException {
        String response = Request.Get("http://localhost:8080/?name=World").execute().returnContent().asString();
        assertThat(response).isEqualTo("JAX-RS says: Hello, World!");
    }
}
