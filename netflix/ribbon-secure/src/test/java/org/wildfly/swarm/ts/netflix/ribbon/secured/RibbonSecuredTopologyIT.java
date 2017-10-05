package org.wildfly.swarm.ts.netflix.ribbon.secured;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCookieStore;

import org.jboss.arquillian.container.test.api.Deployment;

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
import org.keycloak.authorization.client.Configuration;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.util.JsonSerialization;
import org.wildfly.swarm.jaxrs.JAXRSArchive;
import org.wildfly.swarm.keycloak.Secured;
import org.wildfly.swarm.netflix.ribbon.RibbonArchive;
import org.wildfly.swarm.spi.api.JARArchive;
import org.wildfly.swarm.ts.netflix.ribbon.secured.MockTopologyConnector;
import org.wildfly.swarm.ts.netflix.ribbon.secured.MockTopologyConnectorServiceActivator;


@RunWith(Arquillian.class)
public class RibbonSecuredTopologyIT {

    private static final String TESTING_REALM_DISPLAY_NAME="Ribbon secured testing Realm";

    private static AuthzClient authzClient;

    private static Keycloak keycloak;

    private static String userId;

    @Deployment(testable= false)
    public static Archive<?> getDeployment() {

        JAXRSArchive deployment = ShrinkWrap.create(JAXRSArchive.class);

        deployment.addClass(HelloResource.class).addClass(RestApplication.class)
            .addClass(RibbonSecuredClientResource.class).addClass(RibbonTestFallbackHandler.class)
            .addClass(TokenDTO.class)
            .addClass(MockTopologyConnector.class).addClass(MockTopologyConnectorServiceActivator.class)
            .addAsServiceProvider(ServiceActivator.class, MockTopologyConnectorServiceActivator.class);

        deployment.addAsWebInfResource(new File("src/test/resources/keycloak.json"), "keycloak.json");

        deployment.as(JARArchive.class).addModule("org.wildfly.swarm.topology", "runtime"); // needed for mock topology connector
        deployment.as(JARArchive.class).addModule("org.jboss.as.network");                  // needed for mock topology connector
        deployment.as(RibbonArchive.class).advertise("ts-netflix-ribbon-secured");

        deployment.as(Secured.class)
                .protect("/protected")
                .withMethod("GET")
                .withRole("*");

        return deployment;
    }

    @BeforeClass
    public static void setupKeycloakRealm() throws Exception {

        keycloak = Keycloak.getInstance("http://localhost:8180/auth", "master", "admin", "admin", "admin-cli");

        RealmRepresentation realm = new RealmRepresentation();
        realm.setRealm("test-realm");
        realm.setDisplayName(TESTING_REALM_DISPLAY_NAME);
        realm.setEnabled(true);
        realm.setSslRequired("external");
        keycloak.realms().create(realm);

        ClientRepresentation client = new ClientRepresentation();
        client.setClientId("test-client");
        client.setProtocol("openid-connect");
        List<String> uris = new ArrayList<String>();
        uris.add("http://localhost:8080/protected/*");
        client.setRedirectUris(uris);
        client.setClientAuthenticatorType("client-secret");
        client.setSecret("19666a4f-32dd-4049-b082-684c74115f28");
        client.setEnabled(true);

        Response response = keycloak.realms().realm("test-realm").clients().create(client);
        response.close();

        UserRepresentation user = new UserRepresentation();
        user.setUsername("test-user");
        user.setEnabled(true);
        response = keycloak.realms().realm("test-realm").users().create(user);
        userId = getIdOfCreatedUser(response);
        response.close();

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue("test-password");
        credential.setTemporary(false);
        keycloak.realms().realm("test-realm").users().get(userId).resetPassword(credential);

        authzClient = createAuthzCLient("http://localhost:8180/auth");
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

    private static AuthzClient createAuthzCLient(String ssoAuthUrl) throws Exception {
        InputStream configStream = RibbonSecuredTopologyIT.class.getClassLoader().getResourceAsStream("keycloak.json");
        if (configStream == null) {
            throw new IllegalStateException("Could not find any keycloak.json file in classpath.");
        }

        Configuration baseline = JsonSerialization.readValue(
                configStream,
                Configuration.class,
                true
        );

        return AuthzClient.create(baseline);
    }

    @Test
    public void testAccessSecuredContentNoToken() throws Exception {
        Executor executor = Executor.newInstance().use(new BasicCookieStore());

        String response = executor.execute(Request.Get("http://localhost:8080/protected")).
                returnContent().asString();
        assertThat(response).contains(TESTING_REALM_DISPLAY_NAME);
    }

    @Test
    public void testAccessSecuredContentTokenWithFallback() throws Exception {
        Executor executor = Executor.newInstance().use(new BasicCookieStore());

        String token = authzClient.obtainAccessToken("test-user", "test-password").getToken();

        String json = "{\"token\":\"" + token + "\"}";

        String response = executor.execute(Request.Post("http://localhost:8080/test").
                bodyString(json, ContentType.APPLICATION_JSON)).returnContent().asString();
        assertThat(response).contains("Hello, World!");

        Request.Post("http://localhost:8080/disable").execute().discardContent();

        response = Request.Post("http://localhost:8080/test").
                bodyString(json, ContentType.APPLICATION_JSON).execute().returnContent().asString();
        assertThat(response).contains("Fallback string");
    }
}