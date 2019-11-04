package org.wildfly.swarm.ts.javaee8.cdi.spiconfigurators;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.literal.InjectLiteral;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

@ApplicationScoped
@Default
public class CustomExtension implements Extension {

    public void addLegacyServiceAsBean(@Observes BeforeBeanDiscovery bbd) {
        bbd.addAnnotatedType(HelloGetter.class, HelloGetter.class.getName())
                .add(ApplicationScoped.Literal.INSTANCE);
        bbd.addAnnotatedType(LegacyService.class, LegacyService.class.getName())
                .add(ApplicationScoped.Literal.INSTANCE)
                .filterFields(c -> c.getJavaMember().getName().equals("getter"))
                .findFirst().get().add(InjectLiteral.INSTANCE);
    }

}
