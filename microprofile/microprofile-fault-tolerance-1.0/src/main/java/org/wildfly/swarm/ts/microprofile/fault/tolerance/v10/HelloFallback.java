package org.wildfly.swarm.ts.microprofile.fault.tolerance.v10;

import org.eclipse.microprofile.faulttolerance.ExecutionContext;
import org.eclipse.microprofile.faulttolerance.FallbackHandler;

import javax.inject.Inject;

public class HelloFallback implements FallbackHandler<String> {
    @Inject
    private MyContext context;

    @Override
    public String handle(ExecutionContext context) {
        // SWARM-1930
        if ("timeout".equals(context.getMethod().getName())) {
            return "Fallback Hello, context = foobar";
        }

        return "Fallback Hello, context = " + this.context.getValue();
    }
}
