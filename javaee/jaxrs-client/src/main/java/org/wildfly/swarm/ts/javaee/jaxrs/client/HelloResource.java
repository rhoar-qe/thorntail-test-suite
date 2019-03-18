package org.wildfly.swarm.ts.javaee.jaxrs.client;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/hello")
public class HelloResource {
    @GET
    public String get() {
        return "Hello from JAX-RS";
    }
}
