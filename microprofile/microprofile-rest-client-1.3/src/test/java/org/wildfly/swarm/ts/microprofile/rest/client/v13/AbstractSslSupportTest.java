package org.wildfly.swarm.ts.microprofile.rest.client.v13;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.junit.BeforeClass;
import org.wildfly.swarm.ts.microprofile.rest.client.v13.sslsupport.ProgrammaticClient;

public abstract class AbstractSslSupportTest {

    protected static final char[] clientPassword = "client-password".toCharArray();

    protected static final File clientTruststore = new File("target/classes/client-truststore.jks");

    protected static final File clientKeystore = new File("target/classes/client-keystore.jks");

    protected static final File wrongClientKeystore = new File("target/classes/unknown-client-keystore.jks");

    protected static final File wrongClientTruststore = wrongClientKeystore;

    protected static URI baseUri;

    protected static KeyStore correctTruststore;

    protected static KeyStore correctKeystore;

    protected static KeyStore incorrectTruststore;

    protected static KeyStore incorrectKeystore;

    @BeforeClass
    public static void init() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, URISyntaxException {
        correctTruststore = loadKeystore(clientTruststore);
        correctKeystore = loadKeystore(clientKeystore);
        incorrectTruststore = loadKeystore(wrongClientTruststore);
        incorrectKeystore = loadKeystore(wrongClientKeystore);
        baseUri = new URI("https://localhost:8443/rest");
    }

    protected static KeyStore loadKeystore(File filename) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (InputStream is = new FileInputStream(filename)) {
            keystore.load(is, clientPassword);
        }
        return keystore;
    }

    protected ProgrammaticClient getClient(KeyStore truststore, KeyStore keystore, HostnameVerifier verifier) {
        return RestClientBuilder.newBuilder().baseUri(baseUri).trustStore(truststore).keyStore(keystore, new String(clientPassword))
                .hostnameVerifier(verifier).build(ProgrammaticClient.class);
    }

    protected ProgrammaticClient getClient(KeyStore truststore, KeyStore keystore) {
        return RestClientBuilder.newBuilder().baseUri(baseUri).trustStore(truststore).keyStore(keystore, new String(clientPassword))
                .build(ProgrammaticClient.class);
    }

    protected ProgrammaticClient getClient(KeyStore truststore) {
        return RestClientBuilder.newBuilder().baseUri(baseUri).trustStore(truststore).build(ProgrammaticClient.class);
    }

    protected ProgrammaticClient getClient(SSLContext context) {
        return RestClientBuilder.newBuilder().baseUri(baseUri).sslContext(context).build(ProgrammaticClient.class);
    }

}
