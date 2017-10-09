package org.wildfly.swarm.ts.netflix.ribbon;

import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.impl.client.BasicCookieStore;
import org.jboss.arquillian.container.test.api.Deployment;
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

    @Deployment(testable=false)
    public static Archive<?> deployment() {
        JAXRSArchive deployment = ShrinkWrap.create(JAXRSArchive.class);

        deployment.addPackage(RestApplication.class.getPackage());
        deployment.addAsServiceProvider(ServiceActivator.class, MockTopologyConnectorServiceActivator.class);
        deployment.as(JARArchive.class).addModule("org.wildfly.swarm.topology", "runtime"); // needed for mock topology connector
        deployment.as(JARArchive.class).addModule("org.jboss.as.network");                  // needed for mock topology connector
        deployment.as(RibbonArchive.class).advertise("ts-netflix-ribbon");
        return deployment;
    }

    @Test
    public void test() throws Exception {
        Executor executor = Executor.newInstance().use(new BasicCookieStore());

        String response = executor.execute(Request.Get("http://localhost:8080/test")).returnContent().asString();
        assertThat(response).contains("ts-netflix-ribbon");

        executor.execute(Request.Get("http://localhost:8080/disable")); //Emulate 404 in next request. It will force Fallback string thanks to custom HttpResponseValidator
        response = executor.execute(Request.Get("http://localhost:8080/test")).returnContent().asString();

        assertThat(response).contains("Fallback string");
   }
}
