package org.wildfly.swarm.ts.wildfly.topology;

import org.jboss.msc.service.ServiceActivator;
import org.jboss.msc.service.ServiceActivatorContext;
import org.jboss.msc.service.ServiceRegistryException;
import org.jboss.msc.service.ServiceTarget;
import org.wildfly.swarm.topology.runtime.TopologyManager;
import org.wildfly.swarm.topology.runtime.TopologyManagerActivator;

public class MockTopologyConnectorServiceActivator implements ServiceActivator {
    @Override
    public void activate(ServiceActivatorContext context) throws ServiceRegistryException {
        ServiceTarget target = context.getServiceTarget();

        MockTopologyConnector service = new MockTopologyConnector();
        target.addService(TopologyManagerActivator.CONNECTOR_SERVICE_NAME, service)
                .addDependency(TopologyManagerActivator.SERVICE_NAME, TopologyManager.class, service.topology)
                .install();
    }
}
