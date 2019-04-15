package org.wildfly.swarm.ts.microprofile.rest.client.v12;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/simple3")
public class SimpleResource3 {
    @GET
    public Response simpleOperation3() {
        return Response.ok().entity("Hello from endpoint3").build();
    }
}
