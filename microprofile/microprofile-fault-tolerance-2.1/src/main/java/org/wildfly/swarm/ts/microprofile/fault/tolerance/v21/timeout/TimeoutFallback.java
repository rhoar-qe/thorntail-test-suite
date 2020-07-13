package org.wildfly.swarm.ts.microprofile.fault.tolerance.v21.timeout;

import javax.inject.Inject;

import org.eclipse.microprofile.faulttolerance.ExecutionContext;
import org.eclipse.microprofile.faulttolerance.FallbackHandler;

public class TimeoutFallback implements FallbackHandler<String> {
    @Inject
    private TimeoutContext context;

    @Override
    public String handle(ExecutionContext c) {
        return "Fallback Hello, context = " + this.context.getValue();
    }
}

