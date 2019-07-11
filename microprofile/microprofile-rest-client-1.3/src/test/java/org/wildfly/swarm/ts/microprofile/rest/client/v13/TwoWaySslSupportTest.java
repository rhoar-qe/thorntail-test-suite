package org.wildfly.swarm.ts.microprofile.rest.client.v13;

import javax.inject.Inject;
import javax.ws.rs.ProcessingException;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.ts.microprofile.rest.client.v13.sslsupport.TwoWayCDIClient;
import org.wildfly.swarm.ts.microprofile.rest.client.v13.sslsupport.WrongKeystoreCDIClient;


@RunWith(Arquillian.class)
public class TwoWaySslSupportTest extends AbstractSslSupportTest {

    @Inject
    @RestClient
    private TwoWayCDIClient clientTwoWay;

    @Inject
    @RestClient
    private WrongKeystoreCDIClient clientWrongKeystore;

    @Deployment
    public static Archive<?> deployment() {
        WebArchive archive = ShrinkWrap.create(WebArchive.class)
                .addPackages(true, TwoWaySslSupportTest.class.getPackage())
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")

                .addAsManifestResource("client-cert.cer", "client-cert.cer")
                .addAsManifestResource("client-keystore.jks", "client-keystore.jks")
                .addAsManifestResource("client-truststore.jks", "client-truststore.jks")
                .addAsManifestResource("server-cert.cer", "server-cert.cer")
                .addAsManifestResource("server-keystore.jks", "server-keystore.jks")
                .addAsManifestResource("server-truststore.jks", "server-truststore.jks")
                .addAsManifestResource("unknown-client-keystore.jks", "unknown-client-keystore.jks")

                .addAsManifestResource(new ClassLoaderAsset("META-INF/microprofile-config.properties"), "microprofile-config.properties")
                .addAsResource(new ClassLoaderAsset("project-defaults-with-two-way-authentication.yml"), "project-defaults.yml");
        return archive;
    }

    @Test
    public void testCDIWithTrustAndKeystore() {
        String str = clientTwoWay.get();
        Assert.assertEquals("cdi resource", str);
    }

    @Test(expected = ProcessingException.class)
    public void testCDIWithIncorrectKeystore() {
        clientWrongKeystore.get();
    }

    @Test
    @RunAsClient
    public void testProgrammaticWithTruststoreAndKeystore() {
        String str = getClient(correctTruststore, correctKeystore).get();
        Assert.assertEquals("prog resource", str);
    }

    @Test(expected = ProcessingException.class)
    @RunAsClient
    public void testProgrammaticWithIncorrectKeystore() {
        getClient(correctTruststore, incorrectKeystore).get();
    }

}
