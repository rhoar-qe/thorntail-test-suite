package org.wildfly.swarm.ts.wildfly.topology;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.server.CurrentServiceContainer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;
import org.wildfly.swarm.spi.api.annotations.DeploymentModule;
import org.wildfly.swarm.topology.AdvertisementHandle;
import org.wildfly.swarm.topology.Topology;

import javax.naming.NamingException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@RunWith(Arquillian.class)
@DefaultDeployment
@DeploymentModule(name = "org.wildfly.swarm.topology", slot = "runtime") // needed for mock topology connector
@DeploymentModule(name = "org.jboss.as.network")                         // needed for mock topology connector
@DeploymentModule(name = "org.jboss.as.server")                          // needed for awaiting MSC stability
public class TopologyTest {
    /**
     * Note that service advertisements in the topology are in fact MSC services. Therefore, the registrations
     * and unregistrations happen concurrently, on other threads, and awaiting MSC stability is required.
     */
    @Test
    public void test() throws NamingException, InterruptedException {
        Topology topology = Topology.lookup();

        AtomicBoolean listenerWasInvoked = new AtomicBoolean(false);
        topology.addListener(topo -> listenerWasInvoked.set(true));

        assertThat(topology.asMap())
                .as("Topology should have 1 entry")
                .hasSize(1)
                .as("Topology should contain 'hello-test'")
                .containsKey("hello-test");

        AdvertisementHandle handle = topology.advertise("foobar");

        CurrentServiceContainer.getServiceContainer().awaitStability();
        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            assertThat(topology.asMap())
                    .as("Topology should have 2 entries")
                    .hasSize(2)
                    .as("Topology should contain 'foobar'")
                    .containsKey("foobar");

            assertThat(listenerWasInvoked)
                    .as("Topology change listener should be invoked after service was advertised")
                    .isTrue();
        });

        listenerWasInvoked.set(false);

        handle.unadvertise();

        CurrentServiceContainer.getServiceContainer().awaitStability();
        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            assertThat(topology.asMap())
                    .as("Topology should have 1 entry")
                    .hasSize(1)
                    .as("Topology should contain 'hello-test'")
                    .containsKey("hello-test");

            assertThat(listenerWasInvoked)
                    .as("Topology change listener should be invoked after service was unadvertised")
                    .isTrue();
        });
    }
}
