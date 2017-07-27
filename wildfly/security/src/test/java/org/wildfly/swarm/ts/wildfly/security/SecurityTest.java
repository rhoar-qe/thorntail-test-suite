package org.wildfly.swarm.ts.wildfly.security;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.impl.client.BasicCookieStore;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment
public class SecurityTest {
    @Test
    @RunAsClient
    public void ejbNotLoggedIn() throws IOException {
        HttpResponse response = Request.Get("http://localhost:8080/hello").execute().returnResponse();
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(401);
    }

    @Test
    @RunAsClient
    public void ejbLoggedInAsWrongUser() throws IOException {
        Executor executor = Executor.newInstance().auth("user", "user-password");
        HttpResponse response = executor.execute(Request.Get("http://localhost:8080/hello")).returnResponse();
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(500);
    }

    @Test
    @RunAsClient
    public void ejbLoggedInAsCorrectUser() throws IOException {
        Executor executor = Executor.newInstance().auth("admin", "admin-password");
        String response = executor.execute(Request.Get("http://localhost:8080/hello")).returnContent().asString();
        assertThat(response).isEqualTo("Hello, World!");
    }

    @Test
    @RunAsClient
    public void servletNoUsername() throws IOException {
        HttpResponse response = Request.Get("http://localhost:8080/login").execute().returnResponse();
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(500);
    }

    @Test
    @RunAsClient
    public void servletNonexistingUsername() throws IOException {
        HttpResponse response = Request.Get("http://localhost:8080/login?username=nonexisting")
                .execute().returnResponse();
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(500);
    }

    @Test
    @RunAsClient
    public void servletExistingUsernameWrongPassword() throws IOException {
        HttpResponse response = Request.Get("http://localhost:8080/login?username=user&password=wrong")
                .execute().returnResponse();
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(500);
    }

    @Test
    @RunAsClient
    public void servletExistingUsernameCorrectPassword() throws IOException {
        String response = Request.Get("http://localhost:8080/login?username=user&password=user-password")
                .execute().returnContent().asString();
        assertThat(response).isEqualTo("Logged in: user");
    }
}
