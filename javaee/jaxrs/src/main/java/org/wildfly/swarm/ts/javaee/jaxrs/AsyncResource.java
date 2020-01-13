package org.wildfly.swarm.ts.javaee.jaxrs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;

@Path("/async")
public class AsyncResource {
    @GET
    public void get(@Suspended AsyncResponse response) {
        response.resume("Async hello");
    }
}
