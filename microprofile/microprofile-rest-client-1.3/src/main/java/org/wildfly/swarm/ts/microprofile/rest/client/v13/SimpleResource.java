package org.wildfly.swarm.ts.microprofile.rest.client.v13;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/")
public class SimpleResource {
    @GET
    @Path("/simple")
    public Response simpleOperation() {
        return Response.ok().entity("Hello from endpoint").build();
    }
}
