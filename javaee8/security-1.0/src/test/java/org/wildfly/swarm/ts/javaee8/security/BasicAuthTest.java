package org.wildfly.swarm.ts.javaee8.security;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.util.EntityUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.ts.javaee8.security.auth.basic.RestApplication;
import org.wildfly.swarm.ts.javaee8.security.common.CustomIdentityStore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jboss.shrinkwrap.api.ShrinkWrap.create;

@RunWith(Arquillian.class)
public class BasicAuthTest {
    @Deployment
    public static WebArchive createDeployment() {
        return create(WebArchive.class, "BasicAuthTest.war")
                .addPackage(CustomIdentityStore.class.getPackage())
                .addPackage(RestApplication.class.getPackage())
                .addPackages(true, "com.google.common")
                .addAsResource(new File("src/main/resources/project-defaults.yml"));
    }

    @ArquillianResource
    private URL base;

    @Test
    @RunAsClient
    public void servlet_notAuthenticated() throws IOException {
        HttpResponse response = Request.Get(base + "user").execute().returnResponse();
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_UNAUTHORIZED);
        assertThat(response.getFirstHeader("WWW-Authenticate")).isNotNull();
    }

    @Test
    @RunAsClient
    public void servlet_authenticated() throws IOException {
        get("user", "user@example.com", "userPassword",
                HttpStatus.SC_OK, "Successful access to application servlet by user");
    }

    @Test
    @RunAsClient
    public void servlet_insufficientPermissions() throws IOException {
        get("admin", "user@example.com", "userPassword",
                   HttpStatus.SC_FORBIDDEN, null);
    }

    @Test
    @RunAsClient
    public void servlet_wrongPassword() throws IOException {
        get("user", "user@example.com", "wrongPassword",
                   HttpStatus.SC_UNAUTHORIZED, null);
    }

    @Test
    @RunAsClient
    public void jaxrs_roleAdmin() throws IOException {
        get("api", "admin@example.com", "adminPassword",
                   HttpStatus.SC_OK, "GET by user admin in role ADMIN");
    }

    @Test
    @RunAsClient
    public void jaxrs_roleUser() throws IOException {
        get("api", "user@example.com", "userPassword",
                   HttpStatus.SC_OK, "GET by user user in role USER");
    }

    @Test
    @RunAsClient
    public void jaxrs_roleAdmin_permitted() throws IOException {
        post("api/new_value", "admin@example.com", "adminPassword",
                    HttpStatus.SC_OK, "POST new_value by user admin in role ADMIN:true");
    }

    @Test
    @RunAsClient
    public void jaxrs_roleUser_denied() throws IOException {
        post("api/new_value", "user@example.com", "userPassword",
                    HttpStatus.SC_FORBIDDEN, null);
    }

    @Test
    @RunAsClient
    public void testWrongCredentialsApi() throws IOException {
        get("api", "user@example.com", "wrongPassword",
                   HttpStatus.SC_UNAUTHORIZED, null);
    }

    private void get(String path, String username, String password, int expectedStatusCode, String expectedResponse) throws IOException {
        performRequestAndVerifyResponse(Request.Get(base + path), username, password, expectedStatusCode, expectedResponse);
    }

    private void post(String path, String username, String password, int expectedStatusCode, String expectedResponse) throws IOException {
        performRequestAndVerifyResponse(Request.Post(base + path), username, password, expectedStatusCode, expectedResponse);
    }

    private void performRequestAndVerifyResponse(Request request, String username, String password, int expectedStatusCode, String expectedResponse) throws IOException {
        Executor executor = Executor.newInstance().auth(username, password);
        HttpResponse response = executor.execute(request).returnResponse();
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(expectedStatusCode);
        if (expectedStatusCode < 200 || expectedStatusCode > 299) return;
        String body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        assertThat(body).isEqualTo(expectedResponse);
    }
}
