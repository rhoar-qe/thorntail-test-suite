package org.wildfly.swarm.ts.wildfly.topology.arquillian;

import org.jboss.arquillian.container.test.spi.client.deployment.AuxiliaryArchiveAppender;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

public class TopologyTestExtension implements LoadableExtension {
    @Override
    public void register(ExtensionBuilder builder) {
        builder.service(AuxiliaryArchiveAppender.class, TopologyTestAuxiliaryArchiveAppender.class);
    }

    public static class TopologyTestAuxiliaryArchiveAppender implements AuxiliaryArchiveAppender {
        @Override
        public Archive<?> createAuxiliaryArchive() {
            return ShrinkWrap.create(JavaArchive.class)
                    .addPackages(true, "org.assertj", "org.awaitility");
        }
    }
}
