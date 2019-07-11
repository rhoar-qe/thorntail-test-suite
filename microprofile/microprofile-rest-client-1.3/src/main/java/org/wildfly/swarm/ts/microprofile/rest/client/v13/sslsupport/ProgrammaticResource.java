package org.wildfly.swarm.ts.microprofile.rest.client.v13.sslsupport;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("resourceProg")
public class ProgrammaticResource {
    @GET
    public Response get() {
        return Response.ok().entity("prog resource").build();
    }
}
