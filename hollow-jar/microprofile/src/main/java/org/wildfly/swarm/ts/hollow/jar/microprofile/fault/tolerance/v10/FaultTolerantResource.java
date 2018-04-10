package org.wildfly.swarm.ts.hollow.jar.microprofile.fault.tolerance.v10;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.io.IOException;

@Path("/fault-tolerant")
public class FaultTolerantResource {
    @Inject
    private MyContext context;

    @Inject
    private FaultTolerantService hello;

    @GET
    public String get(@QueryParam("operation") String operation,
                      @QueryParam("fail") boolean fail,
                      @QueryParam("context") String context) throws InterruptedException, IOException {

        this.context.setValue(context);

        switch (operation) {
            case "timeout":
                return hello.timeout(fail);
            case "retry":
                return hello.retry(fail);
            case "circuit-breaker":
                return hello.circuitBreaker(fail);
            case "bulkhead":
                return hello.bulkhead(fail);
            default:
                throw new IllegalArgumentException("Unknown operation: " + operation);
        }
    }
}
