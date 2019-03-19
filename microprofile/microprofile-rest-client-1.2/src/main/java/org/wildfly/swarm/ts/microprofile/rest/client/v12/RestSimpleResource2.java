package org.wildfly.swarm.ts.microprofile.rest.client.v12;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/simple2")
public class RestSimpleResource2 {
    @GET
    public Response simpleOperation2() {
        return Response.ok().entity("Hello from endpoint2").build();
    }
}
