package org.wildfly.swarm.ts.microprofile.rest.client.v10;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/simple")
public class SimpleResource {
    @GET
    public Response simpleOperation() {
        return Response.ok().entity("Hello from endpoint").build();
    }
}
