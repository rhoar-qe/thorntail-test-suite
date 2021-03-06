package org.wildfly.swarm.ts.microprofile.jwt.v10;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractMicroProfileJwt10Test {
    private static PrivateKey loadPrivateKey() throws IOException, GeneralSecurityException {
        byte[] bytes = Files.readAllBytes(Paths.get("target/test-classes/private-key.der"));
        return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(bytes));
    }

    private enum Invalidity {
        WRONG_ISSUER,
        WRONG_DATE,
        WRONG_KEY
    }

    private static String createToken(String... groups) throws IOException, GeneralSecurityException {
        return createToken(Date::new, null, groups);
    }

    private static String createToken(Invalidity invalidity, String... groups) throws IOException, GeneralSecurityException {
        return createToken(Date::new, invalidity, groups);
    }

    private static String createToken(Supplier<Date> clock, Invalidity invalidity, String... groups) throws IOException, GeneralSecurityException {
        String issuer = "https://my.auth.server/";
        if (invalidity == Invalidity.WRONG_ISSUER) {
            issuer = "https://wrong/";
        }

        Date now = clock.get();
        Date expiration = new Date(TimeUnit.SECONDS.toMillis(10) + now.getTime());
        if (invalidity == Invalidity.WRONG_DATE) {
            now = new Date(now.getTime() - TimeUnit.DAYS.toMillis(10));
            expiration = new Date(now.getTime() - TimeUnit.DAYS.toMillis(10));
        }

        PrivateKey privateKey = loadPrivateKey();
        if (invalidity == Invalidity.WRONG_KEY) {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            privateKey = keyPair.getPrivate();
        }

        return Jwts.builder()
                .setIssuer(issuer) // iss
                .setId(UUID.randomUUID().toString()) // jti
                .setExpiration(expiration) // exp
                .setIssuedAt(now) // iat
                .setSubject("test_subject_at_example_com") // sub
                .claim("upn", "test-subject@example.com")
                .claim("groups", Arrays.asList(groups))
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();
    }

    private static Response request(String url, String token) throws IOException {
        return Request.Get(url).addHeader("Authorization", "Bearer " + token).execute();
    }

    // ---

    protected abstract boolean isSuperUser();

    protected abstract boolean isMethodWithMissingPermissionsDenied();

    protected abstract boolean isLongerExpirationGracePeriod();

    @Test
    @RunAsClient
    public void secured_everyone_noGroup() throws IOException, GeneralSecurityException {
        String response = request("http://localhost:8080/secured/everyone", createToken()).returnContent().asString();
        assertThat(response).isEqualTo("Hello, test-subject@example.com, your token was issued by https://my.auth.server/, you are in groups [] and you are NOT in role superuser");
    }

    @Test
    @RunAsClient
    public void secured_everyone_viewGroup() throws IOException, GeneralSecurityException {
        String response = request("http://localhost:8080/secured/everyone", createToken("view")).returnContent().asString();
        assertThat(response).isEqualTo("Hello, test-subject@example.com, your token was issued by https://my.auth.server/, you are in groups [view] and you are NOT in role superuser");
    }

    @Test
    @RunAsClient
    public void secured_everyone_adminGroup() throws IOException, GeneralSecurityException {
        String response = request("http://localhost:8080/secured/everyone", createToken("admin")).returnContent().asString();
        if (isSuperUser()) {
            assertThat(response).isEqualTo("Hello, test-subject@example.com, your token was issued by https://my.auth.server/, you are in groups [admin] and you are in role superuser");
        } else {
            assertThat(response).isEqualTo("Hello, test-subject@example.com, your token was issued by https://my.auth.server/, you are in groups [admin] and you are NOT in role superuser");
        }
    }

    @Test
    @RunAsClient
    public void secured_everyone_wrongIssuer() throws IOException, GeneralSecurityException {
        HttpResponse response = request("http://localhost:8080/secured/everyone", createToken(Invalidity.WRONG_ISSUER)).returnResponse();
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(401);
    }

    @Test
    @RunAsClient
    public void secured_everyone_wrongDate() throws IOException, GeneralSecurityException {
        HttpResponse response = request("http://localhost:8080/secured/everyone", createToken(Invalidity.WRONG_DATE)).returnResponse();
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(401);
    }

    @Test
    @RunAsClient
    public void secured_everyone_wrongKey() throws IOException, GeneralSecurityException {
        HttpResponse response = request("http://localhost:8080/secured/everyone", createToken(Invalidity.WRONG_KEY)).returnResponse();
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(401);
    }

    @Test
    @RunAsClient
    public void secured_admin_noGroup() throws IOException, GeneralSecurityException {
        HttpResponse response = request("http://localhost:8080/secured/admin", createToken()).returnResponse();
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(403);
    }

    @Test
    @RunAsClient
    public void secured_admin_viewGroup() throws IOException, GeneralSecurityException {
        HttpResponse response = request("http://localhost:8080/secured/admin", createToken("view")).returnResponse();
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(403);
    }

    @Test
    @RunAsClient
    public void secured_admin_adminGroup() throws IOException, GeneralSecurityException {
        String response = request("http://localhost:8080/secured/admin", createToken("admin")).returnContent().asString();
        assertThat(response).isEqualTo("Restricted area! Admin access granted!");
    }

    @Test
    @RunAsClient
    public void secured_noone_noGroup() throws IOException, GeneralSecurityException {
        HttpResponse response = request("http://localhost:8080/secured/noone", createToken()).returnResponse();
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(403);
    }

    @Test
    @RunAsClient
    public void secured_noone_viewGroup() throws IOException, GeneralSecurityException {
        HttpResponse response = request("http://localhost:8080/secured/noone", createToken("view")).returnResponse();
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(403);
    }

    @Test
    @RunAsClient
    public void secured_noone_adminGroup() throws IOException, GeneralSecurityException {
        HttpResponse response = request("http://localhost:8080/secured/noone", createToken("admin")).returnResponse();
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(403);
    }

    @Test
    @RunAsClient
    public void permitted_correctToken() throws IOException, GeneralSecurityException {
        String response = request("http://localhost:8080/permitted", createToken()).returnContent().asString();
        assertThat(response).isEqualTo("Hello there!");
    }

    @Test
    @RunAsClient
    public void permitted_wrongIssuer() throws IOException, GeneralSecurityException {
        String response = request("http://localhost:8080/permitted", createToken(Invalidity.WRONG_ISSUER)).returnContent().asString();
        assertThat(response).isEqualTo("Hello there!");
    }

    @Test
    @RunAsClient
    public void permitted_wrongDate() throws IOException, GeneralSecurityException {
        String response = request("http://localhost:8080/permitted", createToken(Invalidity.WRONG_DATE)).returnContent().asString();
        assertThat(response).isEqualTo("Hello there!");
    }

    @Test
    @RunAsClient
    public void permitted_wrongKey() throws IOException, GeneralSecurityException {
        String response = request("http://localhost:8080/permitted", createToken(Invalidity.WRONG_KEY)).returnContent().asString();
        assertThat(response).isEqualTo("Hello there!");
    }

    @Test
    @RunAsClient
    public void denied_correctToken() throws IOException, GeneralSecurityException {
        HttpResponse response = request("http://localhost:8080/denied", createToken()).returnResponse();
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(403);
    }

    @Test
    @RunAsClient
    public void mixed_constrained() throws IOException, GeneralSecurityException {
        String response = request("http://localhost:8080/mixed/constrained", createToken()).returnContent().asString();
        assertThat(response).isEqualTo("Constrained method");
    }

    @Test
    @RunAsClient
    public void mixed_unconstrained() throws IOException, GeneralSecurityException {
        if (isMethodWithMissingPermissionsDenied()) {
            HttpResponse response = request("http://localhost:8080/mixed/unconstrained", createToken()).returnResponse();
            assertThat(response.getStatusLine().getStatusCode()).isEqualTo(403);
        } else {
            String response = request("http://localhost:8080/mixed/unconstrained", createToken()).returnContent().asString();
            assertThat(response).isEqualTo("Unconstrained method");
        }
    }

    @Test
    @RunAsClient
    public void contentTypes_plain_plainGroup() throws IOException, GeneralSecurityException {
        String response = Request.Get("http://localhost:8080/content-types")
                .addHeader("Authorization", "Bearer " + createToken("plain"))
                .addHeader("Accept", "text/plain")
                .execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello, world!");
    }

    @Test
    @RunAsClient
    public void contentTypes_plain_webGroup() throws IOException, GeneralSecurityException {
        HttpResponse response = Request.Get("http://localhost:8080/content-types")
                .addHeader("Authorization", "Bearer " + createToken("web"))
                .addHeader("Accept", "text/plain")
                .execute().returnResponse();
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(403);
    }

    @Test
    @RunAsClient
    public void contentTypes_web_webGroup() throws IOException, GeneralSecurityException {
        String response = Request.Get("http://localhost:8080/content-types")
                .addHeader("Authorization", "Bearer " + createToken("web"))
                .addHeader("Accept", "text/html")
                .execute().returnContent().asString();
        assertThat(response).isEqualTo("<html>Hello, world!</html>");
    }

    @Test
    @RunAsClient
    public void contentTypes_web_plainGroup() throws IOException, GeneralSecurityException {
        HttpResponse response = Request.Get("http://localhost:8080/content-types")
                .addHeader("Authorization", "Bearer " + createToken("plain"))
                .addHeader("Accept", "text/html")
                .execute().returnResponse();
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(403);
    }

    @Test
    @RunAsClient
    public void parameterizedPaths_admin_adminGroup() throws IOException, GeneralSecurityException {
        String response = request("http://localhost:8080/parameterized-paths/my/foo/admin", createToken("admin")).returnContent().asString();
        assertThat(response).isEqualTo("Admin accessed foo");
    }

    @Test
    @RunAsClient
    public void parameterizedPaths_admin_viewGroup() throws IOException, GeneralSecurityException {
        HttpResponse response = request("http://localhost:8080/parameterized-paths/my/foo/admin", createToken("view")).returnResponse();
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(403);
    }

    @Test
    @RunAsClient
    public void parameterizedPaths_view_viewGroup() throws IOException, GeneralSecurityException {
        String response = request("http://localhost:8080/parameterized-paths/my/foo/view", createToken("view")).returnContent().asString();
        assertThat(response).isEqualTo("View accessed foo");
    }

    @Test
    @RunAsClient
    public void parameterizedPaths_view_adminGroup() throws IOException, GeneralSecurityException {
        HttpResponse response = request("http://localhost:8080/parameterized-paths/my/foo/view", createToken("admin")).returnResponse();
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(403);
    }

    @Test
    @RunAsClient
    public void tokenExpirationGracePeriod() throws IOException, GeneralSecurityException {
        Supplier<Date> clock = () -> {
            Date now = new Date();
            now = new Date(now.getTime() - TimeUnit.SECONDS.toMillis(90));
            return now;
        };
        String token = createToken(clock, null, "admin");
        if (isLongerExpirationGracePeriod()) {
            String response = request("http://localhost:8080/secured/admin", token).returnContent().asString();
            assertThat(response).isEqualTo("Restricted area! Admin access granted!");
        } else {
            HttpResponse response = request("http://localhost:8080/secured/admin", token).returnResponse();
            assertThat(response.getStatusLine().getStatusCode()).isEqualTo(401);
        }
    }
}
