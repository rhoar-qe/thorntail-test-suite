package org.wildfly.swarm.ts.microprofile.rest.client.v13;

import org.apache.http.ssl.SSLContexts;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.ts.microprofile.rest.client.v13.ssl.AcceptAllVerifier;
import org.wildfly.swarm.ts.microprofile.rest.client.v13.ssl.DenyAllVerifier;
import org.wildfly.swarm.ts.microprofile.rest.client.v13.ssl.OneWaySslCdiClient;
import org.wildfly.swarm.ts.microprofile.rest.client.v13.ssl.OneWaySslCdiClientWithBadTrustStore;
import org.wildfly.swarm.ts.microprofile.rest.client.v13.ssl.ProgrammaticClient;

import javax.inject.Inject;
import javax.net.ssl.SSLContext;
import javax.ws.rs.ProcessingException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
public class OneWaySslSupportTest extends AbstractSslSupportTest {
    @Deployment
    public static Archive<?> deployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addPackage(SimpleResource.class.getPackage())
                .addPackage(OneWaySslCdiClient.class.getPackage())
                .addPackages(true, "org.apache.http")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsManifestResource("client-cert.cer")
                .addAsManifestResource("client-keystore.jks")
                .addAsManifestResource("client-truststore.jks")
                .addAsManifestResource("server-cert.cer")
                .addAsManifestResource("server-keystore.jks")
                .addAsManifestResource("server-truststore.jks")
                .addAsManifestResource("unknown-client-keystore.jks")
                .addAsManifestResource(new ClassLoaderAsset("META-INF/microprofile-config.properties"), "microprofile-config.properties")
                .addAsResource(new ClassLoaderAsset("project-defaults-one-way-ssl.yml"), "project-defaults.yml");
    }

    @Inject
    @RestClient
    private OneWaySslCdiClient client;

    @Inject
    @RestClient
    private OneWaySslCdiClientWithBadTrustStore clientWithBadTrustStore;

    @Test
    public void cdiClient_goodTrustStore() {
        assertEquals("Hello from endpoint", client.get());
    }

    @Test(expected = ProcessingException.class)
    public void cdiClient_badTruststore() {
        clientWithBadTrustStore.get();
    }

    @Test
    public void programmaticClient_goodTrustStore() {
        ProgrammaticClient client = getClient(trustStore);
        assertEquals("Hello from endpoint", client.get());
    }

    @Test
    public void programmaticClient_goodTrustStore_acceptAllVerifier() {
        ProgrammaticClient client = getClient(trustStore, new AcceptAllVerifier());
        assertEquals("Hello from endpoint", client.get());
    }

    @Test(expected = ProcessingException.class)
    public void programmaticClient_goodTrustStore_denyAllVerifier() {
        getClient(trustStore, new DenyAllVerifier()).get();
    }

    @Test
    public void programmaticClient_goodTrustStore_customSslContext() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException, CertificateException, IOException {
        SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(trustStore, null).build();
        ProgrammaticClient client = getClient(sslContext);
        assertEquals("Hello from endpoint", client.get());
    }

    @Test(expected = ProcessingException.class)
    public void programmaticClient_badTrustStore() {
        getClient(badTrustStore).get();
    }
}
