package org.wildfly.swarm.ts.netflix.ribbon;

import org.jboss.msc.service.ServiceActivator;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Test;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.jaxrs.JAXRSArchive;
import org.wildfly.swarm.netflix.ribbon.RibbonArchive;
import org.wildfly.swarm.spi.api.JARArchive;

import com.netflix.ribbon.Ribbon;
import com.netflix.ribbon.RibbonRequest;
import com.netflix.ribbon.http.HttpRequestTemplate;
import com.netflix.ribbon.http.HttpResourceGroup;

import io.netty.buffer.ByteBuf;
import rx.Observable;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

public class RibbonTopologyTest {
    @Test
    public void test() throws Exception {

        Swarm swarm = new Swarm();

        JAXRSArchive deployment = ShrinkWrap.create(JAXRSArchive.class);
        deployment.
            addClass(RestApplication.class).addClass(HelloResource.class).
            addClass(MockTopologyConnector.class).addClass(MockTopologyConnectorServiceActivator.class);
        deployment.addAsServiceProvider(ServiceActivator.class, MockTopologyConnectorServiceActivator.class);
        deployment.as(JARArchive.class).addModule("org.wildfly.swarm.topology", "runtime"); // needed for mock topology connector
        deployment.as(JARArchive.class).addModule("org.jboss.as.network");                  // needed for mock topology connector
        deployment.as(RibbonArchive.class ).advertise("ts-netflix-ribbon");

        try {
            swarm.start().deploy(deployment);

            HttpResourceGroup httpResourceGroup = Ribbon.createHttpResourceGroupBuilder("ribbonTest").build();
            HttpRequestTemplate<ByteBuf> jaxrsTemplate = httpResourceGroup.newTemplateBuilder("ribbonTest", ByteBuf.class)
                    .withUriTemplate("http://localhost:8080/")
                    .withMethod("GET")
                    .withFallbackProvider(new RibbonTestFallbackHandler())
                    .build();
            RibbonRequest<ByteBuf> ribbonRequest = jaxrsTemplate.requestBuilder().build();

            Observable<ByteBuf> observable = ribbonRequest.observe();
            ByteBuf byteBuf = observable.toBlocking().single();

            assertThat(byteBuf.toString(StandardCharsets.UTF_8)).isEqualTo("ts-netflix-ribbon");

            swarm.stop();
            observable = ribbonRequest.observe();
            assertThat(observable.toBlocking().single().toString(StandardCharsets.UTF_8)).isEqualTo("Fallback string");
            swarm = null;
        } finally {
            if (swarm != null) {
                swarm.stop();
            }
        }
    }
}