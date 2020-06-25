package org.wildfly.swarm.ts.microprofile.fault.tolerance.v21;

import java.io.IOException;

import org.apache.http.client.fluent.Request;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment
public class RetryTest {

    @Test
    @RunAsClient
    @InSequence(1)
    public void throwable_retryOnT() throws IOException {
        String response = Request.Get("http://localhost:8080/async?operation=throwable-retryont").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello from throwable_retryOnT, counter: 2");
    }

    @Test
    @RunAsClient
    @InSequence(2)
    public void myException_retryOnT() throws IOException {
        String response = Request.Get("http://localhost:8080/async?operation=myexception-retryont").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello from myException_retryOnT, counter: 2");
    }

    @Test
    @RunAsClient
    @InSequence(3)
    public void throwable_abortOnT() throws IOException {
        String response = Request.Get("http://localhost:8080/async?operation=throwable-abortont").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello from fallback, counter: 1");
    }

    @Test
    @RunAsClient
    @InSequence(4)
    public void myException_abortOnT() throws IOException {
        String response = Request.Get("http://localhost:8080/async?operation=myexception-abortont").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello from fallback, counter: 1");
    }

    @Test
    @RunAsClient
    @InSequence(5)
    public void throwable_retryOnMyE_abortOnT() throws IOException {
        String response = Request.Get("http://localhost:8080/async?operation=throwable-retryonmye-abortont").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello from fallback, counter: 1");
    }

    @Test
    @RunAsClient
    @InSequence(6)
    public void throwable_retryOnT_abortOnMyE() throws IOException {
        String response = Request.Get("http://localhost:8080/async?operation=throwable-retryont-abortonmye").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello from throwable_retryOnT_abortOnMyE, counter: 2");
    }

    @Test
    @RunAsClient
    @InSequence(7)
    public void myException_retryOnMyE_abortOnT() throws IOException {
        String response = Request.Get("http://localhost:8080/async?operation=myexception-retryonmye-abortont").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello from fallback, counter: 1");
    }

    @Test
    @RunAsClient
    @InSequence(8)
    public void myException_retryOnT_abortOnMyE() throws IOException {
        String response = Request.Get("http://localhost:8080/async?operation=myexception-retryont-abortonmye").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello from fallback, counter: 1");
    }
}
