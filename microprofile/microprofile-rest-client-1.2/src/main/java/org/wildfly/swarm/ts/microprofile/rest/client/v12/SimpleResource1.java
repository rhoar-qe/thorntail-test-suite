package org.wildfly.swarm.ts.microprofile.rest.client.v12;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/simple1")
public class SimpleResource1 {
    @GET
    public Response simpleOperation1() {
        return Response.ok().entity("Hello from endpoint1").build();
    }
}
