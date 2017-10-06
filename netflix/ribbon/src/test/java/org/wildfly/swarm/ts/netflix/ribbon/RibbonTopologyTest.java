package org.wildfly.swarm.ts.netflix.ribbon;

import com.netflix.ribbon.Ribbon;
import com.netflix.ribbon.RibbonRequest;
import com.netflix.ribbon.http.HttpRequestTemplate;
import com.netflix.ribbon.http.HttpResourceGroup;
import io.netty.buffer.ByteBuf;
import org.jboss.msc.service.ServiceActivator;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Test;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.jaxrs.JAXRSArchive;
import org.wildfly.swarm.netflix.ribbon.RibbonArchive;
import org.wildfly.swarm.spi.api.JARArchive;
import rx.Observable;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

public class RibbonTopologyTest {
    @Test
    public void test() throws Exception {
        Swarm swarm = new Swarm();

        JAXRSArchive deployment = ShrinkWrap.create(JAXRSArchive.class);
        deployment.addPackage(RestApplication.class.getPackage());
        deployment.addAsServiceProvider(ServiceActivator.class, MockTopologyConnectorServiceActivator.class);
        deployment.as(JARArchive.class).addModule("org.wildfly.swarm.topology", "runtime"); // needed for mock topology connector
        deployment.as(JARArchive.class).addModule("org.jboss.as.network");                  // needed for mock topology connector
        deployment.as(RibbonArchive.class).advertise("ts-netflix-ribbon");

        try {
            swarm.start().deploy(deployment);

            HttpResourceGroup httpResourceGroup = Ribbon.createHttpResourceGroup("ribbonTest");
            HttpRequestTemplate<ByteBuf> httpRequestTemplate = httpResourceGroup.newTemplateBuilder("ribbonTest")
                    .withUriTemplate("http://localhost:8080/")
                    .withMethod("GET")
                    .withFallbackProvider(new RibbonTestFallbackHandler())
                    .build();
            RibbonRequest<ByteBuf> ribbonRequest = httpRequestTemplate.requestBuilder().build();

            Observable<ByteBuf> observable = ribbonRequest.observe();
            assertThat(observable.toBlocking().single().toString(StandardCharsets.UTF_8)).isEqualTo("[ts-netflix-ribbon]");

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
