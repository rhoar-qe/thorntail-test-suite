package org.wildfly.swarm.ts.javaee8.security;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.ts.javaee8.security.auth.form.AuthServlet;
import org.wildfly.swarm.ts.javaee8.security.common.CustomIdentityStore;
import org.wildfly.swarm.ts.javaee8.security.auth.form.AuthConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jboss.shrinkwrap.api.ShrinkWrap.create;

@RunWith(Arquillian.class)
public class FormAuthTest {
    @Deployment
    public static WebArchive createDeployment() {
        return create(WebArchive.class, "FormAuthTest.war")
                .addPackage(AuthServlet.class.getPackage())
                .addPackage(CustomIdentityStore.class.getPackage())
                .addPackages(true, "com.google.common")
                .addAsResource(new File("src/main/resources/project-defaults.yml"))
                .addAsWebResource(new File("src/main/webapp/login.html"));
    }

    @ArquillianResource
    private URL base;

    @Test
    @RunAsClient
    public void testUnauthenticatedRedirectedToLoginPage() throws IOException {
        HttpResponse response = Request.Get(base + "user").execute().returnResponse();
        assertResponse(response, HttpStatus.SC_OK, "Login Form");
    }

    @Test
    @RunAsClient
    public void testAdminAccessAdminServlet() throws IOException {
        try (CloseableHttpClient client = HttpClients.custom().setDefaultCookieStore(new BasicCookieStore()).build()) {
            login(client, "admin@example.com", "adminPassword");

            HttpResponse httpResponse = attemptAccessResource(client, "admin");
            assertResponse(httpResponse, HttpStatus.SC_OK, "Successful access to admin servlet by admin");
        }
    }

    @Test
    @RunAsClient
    public void testAdminAccessApplicationServlet() throws IOException {
        try (CloseableHttpClient client = HttpClients.custom().setDefaultCookieStore(new BasicCookieStore()).build()) {
            login(client, "admin@example.com", "adminPassword");

            HttpResponse httpResponse = attemptAccessResource(client, "user");
            assertResponse(httpResponse, HttpStatus.SC_OK, "Successful access to application servlet by admin");
        }
    }

    @Test
    @RunAsClient
    public void testUserAccessApplicationServlet() throws IOException {
        try (CloseableHttpClient client = HttpClients.custom().setDefaultCookieStore(new BasicCookieStore()).build()) {
            login(client, "user@example.com", "userPassword");

            HttpResponse httpResponse = attemptAccessResource(client, "user");
            assertResponse(httpResponse, HttpStatus.SC_OK, "Successful access to application servlet by user");
        }
    }

    @Test
    @RunAsClient
    public void testUserCantAccessAdminServlet() throws IOException {
        try (CloseableHttpClient client = HttpClients.custom().setDefaultCookieStore(new BasicCookieStore()).build()) {
            login(client, "user@example.com", "userPassword");

            HttpResponse httpResponse = attemptAccessResource(client, "admin");
            assertResponse(httpResponse, HttpStatus.SC_FORBIDDEN, null);
        }
    }

    @Test
    @RunAsClient
    public void testWrongPassword() throws IOException {
        try (CloseableHttpClient client = HttpClients.custom().setDefaultCookieStore(new BasicCookieStore()).build()) {
            HttpResponse authResponse = attemptLogin(client, "user@example.com", "wrongPassword");
            assertResponse(authResponse, HttpStatus.SC_OK, "Authentication: send_failure");

            HttpResponse httpResponse = attemptAccessResource(client, "user");
            assertResponse(httpResponse, HttpStatus.SC_OK, "Login Form");
        }
    }

    @Test
    @RunAsClient
    public void testWrongCredentials() throws IOException {
        try (CloseableHttpClient client = HttpClients.custom().setDefaultCookieStore(new BasicCookieStore()).build()) {
            HttpResponse authResponse = attemptLogin(client, "wrongUser@example.com", "wrongPassword");
            assertResponse(authResponse, HttpStatus.SC_OK, "Authentication: send_failure");

            HttpResponse httpResponse = attemptAccessResource(client, "user");
            assertResponse(httpResponse, HttpStatus.SC_OK, "Login Form");
        }
    }

    private void login(HttpClient client, String email, String password) throws IOException {
        HttpResponse authResponse = attemptLogin(client, email, password);
        assertResponse(authResponse, HttpStatus.SC_OK, "Authentication: success");
    }

    private HttpResponse attemptLogin(HttpClient client, String email, String password) throws IOException {
        HttpUriRequest httpPost = RequestBuilder.post().setUri(base + "auth")
                .addParameter("email", email)
                .addParameter("password", password)
                .build();
        return client.execute(httpPost);
    }

    private HttpResponse attemptAccessResource(HttpClient client, String uri) throws IOException {
        HttpUriRequest httpGet = RequestBuilder.get().setUri(base + uri).build();
        return client.execute(httpGet);
    }

    private void assertResponse(HttpResponse response, int statusCode, String content) throws IOException {
        int actualCode = response.getStatusLine().getStatusCode();
        assertThat(actualCode).isEqualTo(statusCode);
        if (content != null) {
            assertThat(EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8)).contains(content);
        }
    }
}
