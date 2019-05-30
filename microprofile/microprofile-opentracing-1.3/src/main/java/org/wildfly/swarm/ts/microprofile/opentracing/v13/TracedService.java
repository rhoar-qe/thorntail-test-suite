package org.wildfly.swarm.ts.microprofile.opentracing.v13;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.opentracing.Tracer;
import org.eclipse.microprofile.opentracing.Traced;

@ApplicationScoped
public class TracedService {
    @Inject
    private Tracer tracer;

    // 4 combinations of traced and logged
    @Traced
    public String tracedLoggedMethod() {
        tracer.activeSpan().log("tracer: tracedLoggedMethod");
        return "Hello from tracedLoggedMethod";
    }

    @Traced
    public String tracedMethod() {
        return "Hello from tracedMethod";
    }

    public String loggedMethod() {
        tracer.activeSpan().log("tracer: loggedMethod");
        return "Hello from loggedMethod";
    }

    public String notTracedNotLoggedMethod() {
        return "Hello from notTracedNotLoggedMethod";
    }

    // combinations of @Traced attributes: value and operationName
    @Traced(true)
    public String tracedTrue() {
        tracer.activeSpan().log("tracedTrue");
        return "Hello from tracedTrue";
    }

    @Traced(value = true, operationName = "operationName-should-appear-tracedNamedTrue")
    public String tracedNamedTrue() {
        tracer.activeSpan().log("tracedNamedTrue");
        return "Hello from tracedNamedTrue";
    }

    @Traced(false)
    public String tracedFalse() {
        tracer.activeSpan().log("tracedFalse");
        return "Hello from tracedFalse";
    }

    @Traced(value = false, operationName = "should-not-appear-tracedNamedFalse")
    public String tracedNamedFalse() {
        tracer.activeSpan().log("tracedNamedFalse");
        return "Hello from tracedNamedFalse";
    }

    // two wild cards
    @Traced
    public String twoWildcard(long id, String txt) {
        tracer.activeSpan().log("twoWildcard: " + id + ", " + txt);
        return "Hello from twoWildcard: " + id + ", " + txt;
    }
}
