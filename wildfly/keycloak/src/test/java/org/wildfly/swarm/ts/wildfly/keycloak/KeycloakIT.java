package org.wildfly.swarm.ts.wildfly.keycloak;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.message.BasicNameValuePair;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public class KeycloakIT {
    private static final Pattern LOGIN_FORM_URL_PATTERN = Pattern.compile("<form .*? action=\"(.*?)\"");

    private static Keycloak keycloak;

    private static String userId;

    @BeforeClass
    public static void setupKeycloakRealm() {
        keycloak = Keycloak.getInstance("http://localhost:8180/auth", "master", "admin", "admin", "admin-cli");

        {
            RealmRepresentation realm = new RealmRepresentation();
            realm.setRealm("test-realm");
            realm.setDisplayName("Testing Realm");
            realm.setEnabled(true);
            keycloak.realms().create(realm);
        }

        {
            ClientRepresentation client = new ClientRepresentation();
            client.setClientId("test-client");
            client.setProtocol("openid-connect");
            client.setRootUrl("http://localhost:8080/");
            Response response = keycloak.realms().realm("test-realm").clients().create(client);
            response.close();
        }

        {
            UserRepresentation user = new UserRepresentation();
            user.setUsername("test-user");
            user.setEnabled(true);
            Response response = keycloak.realms().realm("test-realm").users().create(user);
            userId = getIdOfCreatedUser(response);
            response.close();
        }

        {
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue("test-password");
            credential.setTemporary(false);
            keycloak.realms().realm("test-realm").users().get(userId).resetPassword(credential);
        }
    }

    @AfterClass
    public static void tearDownKeycloakRealm() {
        keycloak.realms().realm("test-realm").remove();
        keycloak.close();
    }

    private static String getIdOfCreatedUser(Response response) {
        if (!response.getStatusInfo().equals(Response.Status.CREATED)) {
            Response.StatusType statusInfo = response.getStatusInfo();
            throw new IllegalStateException("Creating user failed; expected 201 but returned " + statusInfo.getStatusCode());
        }
        URI location = response.getLocation();
        if (location == null) {
            throw new IllegalStateException("Creating user failed; expected location to be returned");
        }
        String path = location.getPath();
        return path.substring(path.lastIndexOf('/') + 1);
    }

    @Test
    public void publicArea() throws IOException {
        String response = Request.Get("http://localhost:8080/").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello from Public Area, user: null");
    }

    @Test
    public void protectedArea() throws IOException {
        Executor executor = Executor.newInstance().use(new BasicCookieStore());

        String response = executor.execute(Request.Get("http://localhost:8080/")).returnContent().asString();
        assertThat(response).isEqualTo("Hello from Public Area, user: null");

        response = executor.execute(Request.Get("http://localhost:8080/protected")).returnContent().asString();
        assertThat(response).contains("Testing Realm");

        List<NameValuePair> loginForm = Arrays.asList(
                new BasicNameValuePair("username", "test-user"),
                new BasicNameValuePair("password", "test-password")
        );
        Matcher matcher = LOGIN_FORM_URL_PATTERN.matcher(response);
        boolean found = matcher.find();
        assertThat(found).as("Expected login form").isTrue();
        String loginPostUrl = matcher.group(1);
        loginPostUrl = loginPostUrl.replace("&amp;", "&");
        HttpResponse loginResponse = executor.execute(Request.Post(loginPostUrl).bodyForm(loginForm)).returnResponse();

        assertThat(loginResponse.getStatusLine().getStatusCode()).isEqualTo(302);
        String location = loginResponse.getFirstHeader("Location").getValue();
        assertThat(location).as("Expected redirect after login").isNotNull();

        response = executor.execute(Request.Get(location)).returnContent().asString();
        assertThat(response).contains("Hello from Protected Area, user: " + userId);

        response = executor.execute(Request.Get("http://localhost:8080/protected")).returnContent().asString();
        assertThat(response).contains("Hello from Protected Area, user: " + userId);

        response = executor.execute(Request.Get("http://localhost:8080/logout")).returnContent().asString();
        assertThat(response).contains("Hello from Public Area, user: null");
    }
}
