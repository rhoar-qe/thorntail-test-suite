package org.wildfly.swarm.ts.microprofile.rest.client.v13;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.wildfly.swarm.ts.microprofile.rest.client.v13.ssl.ProgrammaticClient;

public abstract class AbstractSslSupportTest {
    protected static final char[] clientPassword = "client-password".toCharArray();

    protected static KeyStore keyStore;
    protected static KeyStore trustStore;
    protected static KeyStore badKeyStore;
    protected static KeyStore badTrustStore;

    protected static URI baseUri;

    static {
        try {
            keyStore = load("/META-INF/client-keystore.jks");
            trustStore = load("/META-INF/client-truststore.jks");
            badKeyStore = load("/META-INF/unknown-client-keystore.jks");
            badTrustStore = badKeyStore;

            baseUri = new URI("https://localhost:8443/rest");
        } catch (URISyntaxException | GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static KeyStore load(String path) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (InputStream is = cl.getResourceAsStream(path)) {
            keystore.load(is, clientPassword);
        }
        return keystore;
    }

    protected final ProgrammaticClient getClient(KeyStore keyStore, KeyStore trustStore) {
        return RestClientBuilder.newBuilder()
                .baseUri(baseUri)
                .keyStore(keyStore, new String(clientPassword))
                .trustStore(trustStore)
                .build(ProgrammaticClient.class);
    }

    protected final ProgrammaticClient getClient(KeyStore trustStore) {
        return RestClientBuilder.newBuilder()
                .baseUri(baseUri)
                .trustStore(trustStore)
                .build(ProgrammaticClient.class);
    }

    protected final ProgrammaticClient getClient(KeyStore trustStore, HostnameVerifier verifier) {
        return RestClientBuilder.newBuilder()
                .baseUri(baseUri)
                .trustStore(trustStore)
                .hostnameVerifier(verifier)
                .build(ProgrammaticClient.class);
    }

    protected final ProgrammaticClient getClient(SSLContext context) {
        return RestClientBuilder.newBuilder()
                .baseUri(baseUri)
                .sslContext(context)
                .build(ProgrammaticClient.class);
    }
}
