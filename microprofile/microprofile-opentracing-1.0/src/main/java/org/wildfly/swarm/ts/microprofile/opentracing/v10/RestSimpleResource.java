package org.wildfly.swarm.ts.microprofile.opentracing.v10;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/")
public class RestSimpleResource {
    @Inject
    private MyService myService;

    @GET
    public Response tracedOperation() {
        return Response.ok().entity(myService.hello()).build();
    }
}
