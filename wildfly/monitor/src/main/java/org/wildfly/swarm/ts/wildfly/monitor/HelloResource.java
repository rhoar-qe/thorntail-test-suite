package org.wildfly.swarm.ts.wildfly.monitor;

import org.wildfly.swarm.health.Health;
import org.wildfly.swarm.health.HealthStatus;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class HelloResource {
    @GET
    public String hello() {
        return "Hello";
    }

    @GET
    @Path("/my-health-check")
    @Health
    public HealthStatus health() {
        return HealthStatus.named("hello").up();
    }
}
