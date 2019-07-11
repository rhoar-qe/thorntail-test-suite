package org.wildfly.swarm.ts.microprofile.rest.client.v13;

import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.inject.spi.CDI;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

@Path("/client")
public class RestClientResource {
    private static final String HTTP_LOCALHOST_8080 = "http://localhost:8080";

    private static final String PASSWORD = "client-password";

    @GET
    @Path("/config-key")
    public String configKey() {
        ClientAutoCloseable client = CDI.current().select(ClientAutoCloseable.class, RestClient.LITERAL).get();
        return client.simpleOperation();
    }

    @GET
    @Path("/missing-config-key")
    public String missingConfigKey() {
        try {
            ClientMissingKey client = CDI.current().select(ClientMissingKey.class, RestClient.LITERAL).get();
            return client.simpleOperation();
        } catch (Exception e) {
            return e.getMessage(); // expecting "Neither baseUri nor baseUrl was specified"
        }
    }

    @GET
    @Path("/auto-close")
    public String autoClose() throws Exception {
        String response;
        ClientAutoCloseable client = RestClientBuilder.newBuilder()
                .baseUrl(new URL(HTTP_LOCALHOST_8080))
                .build(ClientAutoCloseable.class);
        try (ClientAutoCloseable c = client) {
            response = c.simpleOperation();
        }
        // now client is auto-closed
        try {
            response += client.simpleOperation();
        } catch (IllegalStateException e) {
            response += ", client was auto-closed as expected.";
        }
        return response;
    }

    @GET
    @Path("/manual-close")
    public String close() throws Exception {
        String response;
        ClientCloseable client = RestClientBuilder.newBuilder()
                .baseUri(new URI(HTTP_LOCALHOST_8080))
                .build(ClientCloseable.class);
        response = client.simpleOperation();
        client.close();
        // now client is closed
        try {
            response += client.simpleOperation();
        } catch (IllegalStateException e) {
            response += ", client was closed as expected.";
        }
        return response;
    }

    @GET
    @Path("/ssl-client")
    public String sslClient() throws GeneralSecurityException, IOException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        KeyStore keyStore = getKeyStore(cl.getResourceAsStream("/META-INF/client-keystore.jks"));
        KeyStore trustStore = getKeyStore(cl.getResourceAsStream("/META-INF/client-truststore.jks"));

        SslClient client = RestClientBuilder.newBuilder()
                .baseUrl(new URL(HTTP_LOCALHOST_8080))
                .keyStore(keyStore, PASSWORD)
                .trustStore(trustStore)
                .hostnameVerifier(new DefaultHostnameVerifier())
                .build(SslClient.class);
        return "SSL client got: " + client.simpleOperation();
    }

    @GET
    @Path("/ssl-client-cdi")
    public String sslClientCdi() {
        SslClient client = CDI.current().select(SslClient.class, RestClient.LITERAL).get();
        return "SSL client got: " + client.simpleOperation();
    }

    private static KeyStore getKeyStore(InputStream inputStream) throws GeneralSecurityException, IOException {
        KeyStore keystore = KeyStore.getInstance("JKS");
        keystore.load(inputStream, PASSWORD.toCharArray());
        return keystore;
    }
}
