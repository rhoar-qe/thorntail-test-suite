package org.wildfly.swarm.ts.microprofile.opentracing.v10;

import io.opentracing.Tracer;
import org.eclipse.microprofile.opentracing.Traced;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class MyService {
    @Inject
    private Tracer tracer;

    @Traced
    public String hello() {
        tracer.activeSpan().log("Hello tracer");
        return "Hello from traced service";
    }
}
