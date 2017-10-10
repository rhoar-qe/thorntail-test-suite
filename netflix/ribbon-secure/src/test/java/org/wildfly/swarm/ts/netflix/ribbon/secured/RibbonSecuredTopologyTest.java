package org.wildfly.swarm.ts.netflix.ribbon.secured;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.Response;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.message.BasicNameValuePair;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.msc.service.ServiceActivator;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.wildfly.swarm.keycloak.Secured;
import org.wildfly.swarm.netflix.ribbon.RibbonArchive;
import org.wildfly.swarm.spi.api.JARArchive;
import org.wildfly.swarm.ts.netflix.ribbon.secured.MockTopologyConnector;
import org.wildfly.swarm.ts.netflix.ribbon.secured.MockTopologyConnectorServiceActivator;

@RunWith(Arquillian.class)
public class RibbonSecuredTopologyTest {
    
    private static Keycloak keycloak;

    private static String userId;
    
    private static final Pattern LOGIN_FORM_URL_PATTERN = Pattern.compile("<form .*? action=\"(.*?)\"");

    @Deployment(testable=false) 
    public static Archive getDeployment() {

        WebArchive deployment = ShrinkWrap.create(WebArchive.class);
        deployment.addClass(ProtectedAreaServlet.class)
            .addClass(MockTopologyConnector.class).addClass(MockTopologyConnectorServiceActivator.class)
        .addAsServiceProvider(ServiceActivator.class, MockTopologyConnectorServiceActivator.class);

        deployment.addAsWebInfResource(new File("src/main/java/org/wildfly/swarm/ts/netflix/ribbon/secured/keycloak.json"), "keycloak.json");

        deployment.as(JARArchive.class).addModule("org.wildfly.swarm.topology", "runtime"); // needed for mock topology connector
        deployment.as(JARArchive.class).addModule("org.jboss.as.network");                  // needed for mock topology connector
        deployment.as(RibbonArchive.class).advertise("ts-netflix-ribbon-secured");

        deployment.as(Secured.class)
                .protect("/protected/*")
                .withMethod( "GET" )
                .withRole( "*" );

        return deployment;
    }

    @BeforeClass
    public static void setupKeycloakRealm() {
        keycloak = Keycloak.getInstance("http://localhost:8180/auth", "master", "admin", "admin", "admin-cli");

        RealmRepresentation realm = new RealmRepresentation();
        realm.setRealm("test-realm");
        realm.setDisplayName("Testing Realm");
        realm.setEnabled(true);
        keycloak.realms().create(realm);

        ClientRepresentation client = new ClientRepresentation();
        client.setClientId("test-client");
        client.setProtocol("openid-connect");
        List<String> uris = new ArrayList<String>();
        uris.add("http://localhost:8080/protected/*");
        client.setRedirectUris(uris);
        client.setPublicClient(true);
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
    public void test() throws Exception {

        Executor executor = Executor.newInstance().use(new BasicCookieStore());

        String response = executor.execute(Request.Get("http://localhost:8080/protected")).returnContent().asString();
        assertThat(response).contains("Testing Realm");

        List<NameValuePair> loginForm = Arrays.asList(
                new BasicNameValuePair("username", "test-user"),
                new BasicNameValuePair("password", "test-password")
        );
        Matcher matcher = LOGIN_FORM_URL_PATTERN.matcher(response);
        boolean found = matcher.find();
        assertThat(found).as("Expected login form").isTrue();
        String loginPostUrl = matcher.group(1);
        HttpResponse loginResponse = executor.execute(Request.Post(loginPostUrl).bodyForm(loginForm)).returnResponse();

        assertThat(loginResponse.getStatusLine().getStatusCode()).isEqualTo(302);
        String location = loginResponse.getFirstHeader("Location").getValue();
        assertThat(location).as("Expected redirect after login").isNotNull();

        response = executor.execute(Request.Get(location)).returnContent().asString();
        assertThat(response).contains("ts-netflix-ribbon-secured");

    }
}