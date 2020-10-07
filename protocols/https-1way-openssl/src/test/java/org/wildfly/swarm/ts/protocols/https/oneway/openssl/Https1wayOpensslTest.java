package org.wildfly.swarm.ts.protocols.https.oneway.openssl;

import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(Arquillian.class)
@DefaultDeployment
public class Https1wayOpensslTest {
    @Test
    @RunAsClient
    public void http() throws IOException {
        String response = Request.Get("http://localhost:8080/").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello on port 8080, secure: false");
    }

    @Test
    @RunAsClient
    public void https() throws IOException, GeneralSecurityException {
        SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial(new File("target/client-truststore.jks"), "client-password".toCharArray())
                .build();
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLContext(sslContext)
                .setSSLHostnameVerifier(new DefaultHostnameVerifier())
                .build()) {

            String response = Executor.newInstance(httpClient)
                    .execute(Request.Get("https://localhost:8443/"))
                    .returnContent().asString();
            assertThat(response).isEqualTo("Hello on port 8443, secure: true");
        }
    }

    @Test
    @RunAsClient
    public void https_serverCertificateUnknownToClient() throws IOException {
        SSLContext sslContext = SSLContexts.createDefault();
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLContext(sslContext)
                .setSSLHostnameVerifier(new DefaultHostnameVerifier())
                .build()) {

            assertThatThrownBy(() -> {
                Executor.newInstance(httpClient).execute(Request.Get("https://localhost:8443/"));
            }).isExactlyInstanceOf(SSLHandshakeException.class);
        }
    }
}
