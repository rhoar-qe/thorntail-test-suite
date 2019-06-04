package org.wildfly.swarm.ts.netflix.ribbon.secured;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.wildfly.swarm.arquillian.DefaultDeployment;
import org.wildfly.swarm.spi.api.annotations.DeploymentModule;
import org.wildfly.swarm.ts.common.docker.Docker;
import org.wildfly.swarm.ts.common.docker.DockerContainers;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This is only {@code *IT} so that it runs between Docker container
 * starting and stopping Keycloak.
 */
@RunWith(Arquillian.class)
@DefaultDeployment
@DeploymentModule(name = "org.wildfly.swarm.topology", slot = "runtime") // needed for mock topology connector
@DeploymentModule(name = "org.jboss.as.network") // needed for mock topology connector
public class RibbonSecuredTopologyIT {
    @ClassRule
    public static Docker keycloakContainer = DockerContainers.keycloak();

    private static Keycloak keycloak;

    private static AuthzClient authzClient;

    @BeforeClass
    public static void setupKeycloak() {
        keycloak = Keycloak.getInstance("http://localhost:8180/auth", "master", "admin", "admin", "admin-cli");

        {
            RealmRepresentation realm = new RealmRepresentation();
            realm.setRealm("test-realm");
            realm.setDisplayName("Testing Realm");
            realm.setEnabled(true);
            realm.setSslRequired("external");
            keycloak.realms().create(realm);
        }

        {
            ClientRepresentation client = new ClientRepresentation();
            client.setClientId("test-client");
            client.setProtocol("openid-connect");
            client.setClientAuthenticatorType("client-secret");
            client.setSecret("19666a4f-32dd-4049-b082-684c74115f28");
            client.setEnabled(true);
            Response response = keycloak.realms().realm("test-realm").clients().create(client);
            response.close();
        }

        String userId;
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

        authzClient = AuthzClient.create();
    }

    @AfterClass
    public static void tearDownKeycloak() {
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

    @Before
    @RunAsClient
    public void setUp() throws IOException {
        Request.Post("http://localhost:8080/hello/enable").execute().discardContent();
    }

    @Test
    @RunAsClient
    public void directAccess_withoutToken() throws Exception {
        HttpResponse response = Request.Get("http://localhost:8080/hello")
                .execute().returnResponse();
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(400);
    }

    @Test
    @RunAsClient
    public void directAccess_withToken_serviceEnabled() throws Exception {
        String token = authzClient.obtainAccessToken("test-user", "test-password").getToken();

        String response = Request.Get("http://localhost:8080/hello")
                .addHeader("Authorization", "Bearer " + token)
                .execute().returnContent().asString();
        assertThat(response).contains("Hello, World!");
    }

    @Test
    @RunAsClient
    public void directAccess_withToken_serviceDisabled() throws Exception {
        Request.Post("http://localhost:8080/hello/disable").execute().discardContent();

        String token = authzClient.obtainAccessToken("test-user", "test-password").getToken();

        HttpResponse response = Request.Get("http://localhost:8080/hello")
                .addHeader("Authorization", "Bearer " + token)
                .execute().returnResponse();
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(500);
    }

    @Test
    @RunAsClient
    public void accessThroughRibbon_withoutToken() throws Exception {
        HttpResponse response = Request.Get("http://localhost:8080/test")
                .execute().returnResponse();
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(500);
    }

    @Test
    @RunAsClient
    public void accessThroughRibbon_withToken_serviceEnabled() throws Exception {
        String token = authzClient.obtainAccessToken("test-user", "test-password").getToken();

        String response = Request.Get("http://localhost:8080/test")
                .addHeader("Authorization", "Bearer " + token)
                .execute().returnContent().asString();
        assertThat(response).contains("Hello, World!");
    }

    @Test
    @RunAsClient
    public void accessThroughRibbon_withToken_serviceDisabled() throws Exception {
        Request.Post("http://localhost:8080/hello/disable").execute().discardContent();

        String token = authzClient.obtainAccessToken("test-user", "test-password").getToken();

        String response = Request.Get("http://localhost:8080/test")
                .addHeader("Authorization", "Bearer " + token)
                .execute().returnContent().asString();
        assertThat(response).contains("Fallback string");
    }
}
