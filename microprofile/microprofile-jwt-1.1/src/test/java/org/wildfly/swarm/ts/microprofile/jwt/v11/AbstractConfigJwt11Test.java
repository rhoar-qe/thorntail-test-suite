package org.wildfly.swarm.ts.microprofile.jwt.v11;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test support for configuring the public key and issuer needed for
 * verification of the MP-JWT using MicroProfile Config:
 * mp.jwt.verify.publickey
 * mp.jwt.verify.publickey.location
 * mp.jwt.verify.issuer
 * https://github.com/eclipse/microprofile-jwt-auth/releases/tag/1.1
 */
public abstract class AbstractConfigJwt11Test {
    // mp.jwt.verify.issuer same in resources/microprofile-config.properties
    static final String ISSUER = "https://my.auth.server/";

    private static PrivateKey loadPrivateKey() throws IOException, GeneralSecurityException {
        byte[] bytes = Files.readAllBytes(Paths.get("target/test-classes/private-key.der"));
        return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(bytes));
    }

    private static String createToken(String... groups) throws IOException, GeneralSecurityException {

        Date now = new Date();
        Date expiration = new Date(TimeUnit.SECONDS.toMillis(100) + now.getTime());
        PrivateKey privateKey = loadPrivateKey();

        return Jwts.builder()
                .setIssuer(ISSUER) // iss
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

    @Test
    @RunAsClient
    public void configPropertyUsage() throws IOException, GeneralSecurityException {
        // check loaded issuer as @ConfigProperty(name = "mp.jwt.verify.issuer")
        String response = request("http://localhost:8080/issuer?source=property", createToken("view")).returnContent().asString();
        assertThat(response).isEqualTo("Hello, test-subject@example.com, your token was issued by " + ISSUER + ", you are in groups [view] and you are NOT in role superuser");
    }

    @Test
    @RunAsClient
    public void configValueUsage() throws IOException, GeneralSecurityException {
        // loaded from config config.getValue("mp.jwt.verify.issuer")
        String response = request("http://localhost:8080/issuer?source=value", createToken("view")).returnContent().asString();
        assertThat(response).isEqualTo("Hello, test-subject@example.com, your token was issued by " + ISSUER + ", you are in groups [view] and you are NOT in role superuser");
    }
}
