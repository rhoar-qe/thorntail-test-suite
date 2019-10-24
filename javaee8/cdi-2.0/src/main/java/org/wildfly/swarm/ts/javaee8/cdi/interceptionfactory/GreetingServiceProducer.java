package org.wildfly.swarm.ts.javaee8.cdi.interceptionfactory;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InterceptionFactory;
import javax.enterprise.util.AnnotationLiteral;

@Alternative
@ApplicationScoped
@Priority(1)
public class GreetingServiceProducer {
    @Produces
    @RequestScoped
    public GreetingService produce(InterceptionFactory<MyGreetingService> interceptionFactory) {
        interceptionFactory.configure().add(new AnnotationLiteral<CustomInterceptorBinding>() {
        });
        return interceptionFactory.createInterceptedInstance(new MyGreetingService());
    }
}
