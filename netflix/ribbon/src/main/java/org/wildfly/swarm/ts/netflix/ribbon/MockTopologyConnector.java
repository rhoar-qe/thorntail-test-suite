package org.wildfly.swarm.ts.netflix.ribbon;

import org.jboss.as.network.SocketBinding;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.msc.value.InjectedValue;
import org.wildfly.swarm.topology.TopologyConnector;
import org.wildfly.swarm.topology.runtime.Registration;
import org.wildfly.swarm.topology.runtime.TopologyManager;

public class MockTopologyConnector implements TopologyConnector, Service<MockTopologyConnector> {
    InjectedValue<TopologyManager> topology = new InjectedValue<>();

    @Override
    public void advertise(String name, SocketBinding binding, String... tags) throws Exception {
        Registration registration = new Registration("Mock", name,
                binding.getAddress().getHostAddress(), binding.getAbsolutePort(), tags);
        topology.getValue().register(registration);
    }

    @Override
    public void unadvertise(String name, SocketBinding binding) throws Exception {
        TopologyManager topology = this.topology.getValue();
        topology.registrationsForService(name)
                .stream()
                .filter(r -> r.getAddress().equals(binding.getAddress().getHostAddress())
                        && r.getPort() == binding.getAbsolutePort())
                .forEach(topology::unregister);
    }

    @Override
    public void start(StartContext context) throws StartException {
    }

    @Override
    public void stop(StopContext context) {
    }

    @Override
    public MockTopologyConnector getValue() throws IllegalStateException, IllegalArgumentException {
        return this;
    }
}
