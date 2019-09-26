package org.wildfly.swarm.ts.protocols.https.twoway.elytron;

import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(Arquillian.class)
@DefaultDeployment
public class Https2wayElytronTest {
    private static final String[] SUPPORTED_PROTOCOLS = {"TLSv1.2"};

    @Test
    @RunAsClient
    public void http() throws IOException {
        String response = Request.Get("http://localhost:8080/").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello on port 8080, secure: false");
    }

    @Test
    @RunAsClient
    public void https() throws IOException, GeneralSecurityException {
        char[] clientPassword = "client-password".toCharArray();
        SSLContext sslContext = SSLContexts.custom()
                .loadKeyMaterial(new File("target/client-keystore.jks"), clientPassword, clientPassword)
                .loadTrustMaterial(new File("target/client-truststore.jks"), clientPassword)
                .build();
        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext,
                SUPPORTED_PROTOCOLS, null, new DefaultHostnameVerifier());
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(sslSocketFactory)
                .build()) {

            String response = Executor.newInstance(httpClient)
                    .execute(Request.Get("https://localhost:8443/"))
                    .returnContent().asString();
            assertThat(response).isEqualTo("Hello on port 8443, secure: true");
        }
    }

    @Test
    @RunAsClient
    public void https_serverCertificateUnknownToClient() throws IOException, GeneralSecurityException {
        char[] clientPassword = "client-password".toCharArray();
        SSLContext sslContext = SSLContexts.custom()
                .loadKeyMaterial(new File("target/client-keystore.jks"), clientPassword, clientPassword)
                .build();
        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext,
                SUPPORTED_PROTOCOLS, null, new DefaultHostnameVerifier());
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(sslSocketFactory)
                .build()) {

            assertThatThrownBy(() -> {
                Executor.newInstance(httpClient).execute(Request.Get("https://localhost:8443/"));
            }).isInstanceOf(SSLException.class);
        }
    }

    @Test
    @RunAsClient
    public void https_clientCertificateUnknownToServer() throws IOException, GeneralSecurityException {
        char[] clientPassword = "client-password".toCharArray();
        SSLContext sslContext = SSLContexts.custom()
                .loadKeyMaterial(new File("target/unknown-client-keystore.jks"), clientPassword, clientPassword)
                .loadTrustMaterial(new File("target/client-truststore.jks"), clientPassword)
                .build();
        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext,
                SUPPORTED_PROTOCOLS, null, new DefaultHostnameVerifier());
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(sslSocketFactory)
                .build()) {

            assertThatThrownBy(() -> {
                Executor.newInstance(httpClient).execute(Request.Get("https://localhost:8443/"));
            }).isInstanceOf(SSLException.class);
        }
    }

    @Test
    @RunAsClient
    public void https_serverCertificateUnknownToClient_clientCertificateUnknownToServer() throws IOException, GeneralSecurityException {
        char[] clientPassword = "client-password".toCharArray();
        SSLContext sslContext = SSLContexts.custom()
                .loadKeyMaterial(new File("target/unknown-client-keystore.jks"), clientPassword, clientPassword)
                .build();
        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext,
                SUPPORTED_PROTOCOLS, null, new DefaultHostnameVerifier());
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(sslSocketFactory)
                .build()) {

            assertThatThrownBy(() -> {
                Executor.newInstance(httpClient).execute(Request.Get("https://localhost:8443/"));
            }).isInstanceOf(SSLException.class);
        }
    }
}
