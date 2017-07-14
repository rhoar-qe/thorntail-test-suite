package org.wildfly.swarm.ts.wildfly.msc;

import org.jboss.msc.service.ServiceActivator;
import org.jboss.msc.service.ServiceActivatorContext;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ServiceRegistryException;
import org.jboss.msc.service.ServiceTarget;

public class HelloServiceActivator implements ServiceActivator {
    @Override
    public void activate(ServiceActivatorContext serviceActivatorContext) throws ServiceRegistryException {
        System.out.println(HelloServiceActivator.class.getSimpleName() + " running");

        ServiceTarget target = serviceActivatorContext.getServiceTarget();

        HelloService service = new HelloService();
        target.addService(ServiceName.of("test.hello"), service)
                .setInitialMode(ServiceController.Mode.ACTIVE)
                .addDependency(ServiceName.of("wildfly", "swarm", "main-args"), String[].class, service.mainArgs)
                .install();

    }
}
