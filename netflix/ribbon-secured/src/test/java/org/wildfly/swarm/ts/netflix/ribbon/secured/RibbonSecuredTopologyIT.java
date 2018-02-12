package org.wildfly.swarm.ts.netflix.ribbon.secured;

import org.apache.http.client.fluent.Request;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.msc.service.ServiceActivator;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.wildfly.swarm.jaxrs.JAXRSArchive;
import org.wildfly.swarm.keycloak.Secured;
import org.wildfly.swarm.netflix.ribbon.RibbonArchive;
import org.wildfly.swarm.spi.api.JARArchive;

import javax.ws.rs.core.Response;
import java.io.File;
import java.net.URI;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This is only {@code *IT} so that it runs between the Docker Maven plugin starting and stopping Keycloak.
 */
@RunWith(Arquillian.class)
public class RibbonSecuredTopologyIT {
    private static Keycloak keycloak;

    private static AuthzClient authzClient;

    @Deployment(testable = false)
    public static Archive<?> getDeployment() {
        JAXRSArchive deployment = ShrinkWrap.create(JAXRSArchive.class)
                .addPackage(RestApplication.class.getPackage())
                .addAsServiceProvider(ServiceActivator.class, MockTopologyConnectorServiceActivator.class);

        deployment.addAsWebInfResource(new File("src/test/resources/keycloak.json"), "keycloak.json");

        deployment.as(JARArchive.class).addModule("org.wildfly.swarm.topology", "runtime"); // needed for mock topology connector
        deployment.as(JARArchive.class).addModule("org.jboss.as.network");                  // needed for mock topology connector
        deployment.as(RibbonArchive.class).advertise("ts-netflix-ribbon-secured");

        deployment.as(Secured.class)
                .protect("/hello")
                .withMethod("GET")
                .withRole("*");

        return deployment;
    }

    @BeforeClass
    public static void setupKeycloakRealm() {
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
            client.setRedirectUris(Collections.singletonList("http://localhost:8080/protected/*"));
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
    @RunAsClient
    public void testAccessSecuredContentTokenWithFallback() throws Exception {
        String token = authzClient.obtainAccessToken("test-user", "test-password").getToken();

        String response = Request.Get("http://localhost:8080/test")
                .addHeader("Authorization", "Bearer " + token)
                .execute().returnContent().asString();
        assertThat(response).contains("Hello, World!");

        Request.Post("http://localhost:8080/hello/disable").execute().discardContent();

        response = Request.Get("http://localhost:8080/test")
                .addHeader("Authorization", "Bearer " + token)
                .execute().returnContent().asString();
        assertThat(response).contains("Fallback string");
    }
}
