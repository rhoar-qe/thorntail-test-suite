package org.wildfly.swarm.ts.javaee.jaxrs.client.json;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hello")
public class HelloResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Hello get() {
        return new Hello("Hello in JSON");
    }
}
