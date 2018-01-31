package org.wildfly.swarm.ts.javaee.cdi.bean.validation.arquillian;

import org.jboss.arquillian.container.test.spi.client.deployment.AuxiliaryArchiveAppender;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

public class CdiBeanValidationTestExtension implements LoadableExtension {
    @Override
    public void register(ExtensionBuilder builder) {
        builder.service(AuxiliaryArchiveAppender.class, CdiBeanValidationTestAuxiliaryArchiveAppender.class);
    }

    public static class CdiBeanValidationTestAuxiliaryArchiveAppender implements AuxiliaryArchiveAppender {
        @Override
        public Archive<?> createAuxiliaryArchive() {
            return ShrinkWrap.create(JavaArchive.class)
                    .addPackages(true, "org.assertj");
        }
    }
}
