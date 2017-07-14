package org.wildfly.swarm.ts.wildfly.msc;

import org.jboss.msc.service.Service;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.msc.value.InjectedValue;

import java.util.Arrays;
import java.util.logging.Logger;

public class HelloService implements Service<Void> {
    private static final Logger log = Logger.getLogger(HelloService.class.getSimpleName());

    InjectedValue<String[]> mainArgs = new InjectedValue<>();

    @Override
    public void start(StartContext context) throws StartException {
        log.info(this.getClass().getSimpleName() + " started, args: " + Arrays.toString(mainArgs.getOptionalValue()));
    }

    @Override
    public void stop(StopContext context) {
    }

    @Override
    public Void getValue() throws IllegalStateException, IllegalArgumentException {
        return null;
    }
}
