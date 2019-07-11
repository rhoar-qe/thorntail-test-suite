package org.wildfly.swarm.ts.microprofile.rest.client.v13;

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
import org.wildfly.swarm.ts.microprofile.rest.client.v13.ssl.ProgrammaticClient;
import org.wildfly.swarm.ts.microprofile.rest.client.v13.ssl.TwoWaySslCdiClient;
import org.wildfly.swarm.ts.microprofile.rest.client.v13.ssl.TwoWaySslCdiClientWithBadKeyStore;

import javax.inject.Inject;
import javax.ws.rs.ProcessingException;

import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
public class TwoWaySslSupportTest extends AbstractSslSupportTest {
    @Deployment
    public static Archive<?> deployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addPackage(SimpleResource.class.getPackage())
                .addPackage(TwoWaySslCdiClient.class.getPackage())
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
                .addAsResource(new ClassLoaderAsset("project-defaults.yml"), "project-defaults.yml");
    }

    @Inject
    @RestClient
    private TwoWaySslCdiClient client;

    @Inject
    @RestClient
    private TwoWaySslCdiClientWithBadKeyStore clientWithBadKeyStore;

    @Test
    public void cdiClient_goodKeyStore() {
        assertEquals("Hello from endpoint", client.get());
    }

    @Test(expected = ProcessingException.class)
    public void cdiClient_badKeyStore() {
        clientWithBadKeyStore.get();
    }

    @Test
    public void programmaticClient_goodKeyStore() {
        ProgrammaticClient client = getClient(keyStore, trustStore);
        assertEquals("Hello from endpoint", client.get());
    }

    @Test(expected = ProcessingException.class)
    public void programmaticClient_badKeyStore() {
        getClient(badKeyStore, trustStore).get();
    }
}
