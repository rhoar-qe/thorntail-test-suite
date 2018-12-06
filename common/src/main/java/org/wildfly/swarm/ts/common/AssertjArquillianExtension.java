package org.wildfly.swarm.ts.common;

import org.jboss.arquillian.container.test.spi.client.deployment.AuxiliaryArchiveAppender;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

/**
 * Adds the AssertJ package to the deployment for in-container tests.
 */
public class AssertjArquillianExtension implements LoadableExtension {
    @Override
    public void register(ExtensionBuilder builder) {
        builder.service(AuxiliaryArchiveAppender.class, AssertjAuxiliaryArchiveAppender.class);
    }

    public static class AssertjAuxiliaryArchiveAppender implements AuxiliaryArchiveAppender {
        @Override
        public Archive<?> createAuxiliaryArchive() {
            return ShrinkWrap.create(JavaArchive.class)
                    .addPackages(true, "org.assertj");
        }
    }
}
