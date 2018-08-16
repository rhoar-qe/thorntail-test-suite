package org.wildfly.swarm.ts.microprofile.opentracing.v10;

import org.eclipse.microprofile.opentracing.Traced;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/")
public class RestSimpleResource {
    @GET
    @Traced
    public Response tracedOperation() {
        return Response.ok().entity("Hello from traced endpoint").build();
    }
}
