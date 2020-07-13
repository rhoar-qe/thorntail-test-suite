package org.wildfly.swarm.ts.microprofile.fault.tolerance.v21;

import java.io.IOException;

import org.apache.http.client.fluent.Request;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment
public class TimeoutTest {
    @Test
    @RunAsClient
    public void timeoutOk() throws IOException {
        String response = Request.Get("http://localhost:8080/timeout?context=foobar").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello from @Timeout method, context = foobar");
    }

    @Test
    @RunAsClient
    public void timeoutFailure() throws IOException {
        String response = Request.Get("http://localhost:8080/timeout?context=foobar&fail=true").execute().returnContent().asString();
        assertThat(response).isEqualTo("Fallback Hello, context = foobar");
    }

    @Test
    @RunAsClient
    public void timeoutOkAsync() throws IOException {
        String response = Request.Get("http://localhost:8080/timeout?operation=async&context=foobar").execute().returnContent().asString();
        assertThat(response).isEqualTo("Async Hello from @Timeout method, context = foobar");
    }

    @Test
    @RunAsClient
    public void timeoutFailureAsync() throws IOException {
        String response = Request.Get("http://localhost:8080/timeout?operation=async&context=foobar&fail=true").execute().returnContent().asString();
        assertThat(response).isEqualTo("Async Fallback Hello, context = foobar");
    }
}
