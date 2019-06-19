package org.wildfly.swarm.ts.protocols.https.twoway.authz;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
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
public class Https2wayAuthzTest {
    @Test
    @RunAsClient
    public void http() throws IOException {
        String response = Request.Get("http://localhost:8080/").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello on port 8080, secure: false, principal: null, isGuest: false, isUser: false");
    }

    @Test
    @RunAsClient
    public void https_authenticatedAndAuthorizedClient() throws IOException, GeneralSecurityException {
        char[] clientPassword = "client-password".toCharArray();
        SSLContext sslContext = SSLContexts.custom()
                .loadKeyMaterial(new File("target/client-keystore.jks"), clientPassword, clientPassword)
                .loadTrustMaterial(new File("target/client-truststore.jks"), clientPassword)
                .build();
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLContext(sslContext)
                .setSSLHostnameVerifier(new DefaultHostnameVerifier())
                .build()) {

            Executor executor = Executor.newInstance(httpClient);

            {
                String response = executor.execute(Request.Get("https://localhost:8443/")).returnContent().asString();
                assertThat(response).isEqualTo("Hello on port 8443, secure: true, principal: CN=client, isGuest: false, isUser: true");
            }

            {
                String response = executor.execute(Request.Get("https://localhost:8443/secured")).returnContent().asString();
                assertThat(response).isEqualTo("Client certificate: CN=client");
            }
        }
    }

    @Test
    @RunAsClient
    public void https_authenticatedButUnauthorizedClient() throws IOException, GeneralSecurityException {
        char[] clientPassword = "client-password".toCharArray();
        SSLContext sslContext = SSLContexts.custom()
                .loadKeyMaterial(new File("target/guest-client-keystore.jks"), clientPassword, clientPassword)
                .loadTrustMaterial(new File("target/client-truststore.jks"), clientPassword)
                .build();
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLContext(sslContext)
                .setSSLHostnameVerifier(new DefaultHostnameVerifier())
                .build()) {

            Executor executor = Executor.newInstance(httpClient);

            {
                String response = executor.execute(Request.Get("https://localhost:8443/")).returnContent().asString();
                assertThat(response).isEqualTo("Hello on port 8443, secure: true, principal: CN=guest-client, isGuest: true, isUser: false");
            }

            {
                HttpResponse response = executor.execute(Request.Get("https://localhost:8443/secured")).returnResponse();
                assertThat(response.getStatusLine().getStatusCode()).isEqualTo(403);
            }
        }
    }

    @Test
    @RunAsClient
    public void https_serverCertificateUnknownToClient() throws IOException, GeneralSecurityException {
        char[] clientPassword = "client-password".toCharArray();
        SSLContext sslContext = SSLContexts.custom()
                .loadKeyMaterial(new File("target/client-keystore.jks"), clientPassword, clientPassword)
                .build();
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLContext(sslContext)
                .setSSLHostnameVerifier(new DefaultHostnameVerifier())
                .build()) {

            assertThatThrownBy(() -> {
                Executor.newInstance(httpClient).execute(Request.Get("https://localhost:8443/"));
            }).isExactlyInstanceOf(SSLHandshakeException.class);
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
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLContext(sslContext)
                .setSSLHostnameVerifier(new DefaultHostnameVerifier())
                .build()) {

            Executor executor = Executor.newInstance(httpClient);

            {
                String response = executor.execute(Request.Get("https://localhost:8443/")).returnContent().asString();
                assertThat(response).isEqualTo("Hello on port 8443, secure: true, principal: null, isGuest: false, isUser: false");
            }

            {
                HttpResponse response = executor.execute(Request.Get("https://localhost:8443/secured")).returnResponse();
                assertThat(response.getStatusLine().getStatusCode()).isEqualTo(403);
            }
        }
    }

    @Test
    @RunAsClient
    public void https_serverCertificateUnknownToClient_clientCertificateUnknownToServer() throws IOException, GeneralSecurityException {
        char[] clientPassword = "client-password".toCharArray();
        SSLContext sslContext = SSLContexts.custom()
                .loadKeyMaterial(new File("target/unknown-client-keystore.jks"), clientPassword, clientPassword)
                .build();
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
