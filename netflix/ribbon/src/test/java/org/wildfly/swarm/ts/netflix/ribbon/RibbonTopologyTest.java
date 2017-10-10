package org.wildfly.swarm.ts.netflix.ribbon;

import org.apache.http.client.fluent.Request;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.msc.service.ServiceActivator;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.jaxrs.JAXRSArchive;
import org.wildfly.swarm.netflix.ribbon.RibbonArchive;
import org.wildfly.swarm.spi.api.JARArchive;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class RibbonTopologyTest {
    @Deployment
    public static Archive<?> deployment() {
        JAXRSArchive war = ShrinkWrap.create(JAXRSArchive.class)
                .addPackage(RestApplication.class.getPackage())
                .addAsServiceProvider(ServiceActivator.class, MockTopologyConnectorServiceActivator.class);
        war.as(JARArchive.class).addModule("org.wildfly.swarm.topology", "runtime"); // needed for mock topology connector
        war.as(JARArchive.class).addModule("org.jboss.as.network");                  // needed for mock topology connector
        war.as(RibbonArchive.class).advertise("ts-netflix-ribbon");
        return war;
    }

    @Test
    @RunAsClient
    public void test() throws Exception {
        String response = Request.Get("http://localhost:8080/test").execute().returnContent().asString();
        assertThat(response).contains("Hello, World!");

        Request.Post("http://localhost:8080/hello/disable").execute().discardContent();

        response = Request.Get("http://localhost:8080/test").execute().returnContent().asString();
        assertThat(response).contains("Fallback string");
    }
}
