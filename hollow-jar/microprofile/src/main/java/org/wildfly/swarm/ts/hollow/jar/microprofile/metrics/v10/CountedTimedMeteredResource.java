package org.wildfly.swarm.ts.hollow.jar.microprofile.metrics.v10;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/counted-timed-metered")
public class CountedTimedMeteredResource {
    @Inject
    private CountedTimedMeteredService hello;

    @GET
    public Response op() {
        try {
            return Response.ok().entity(hello.hello()).build();
        } catch (InterruptedException e) {
            return Response.serverError().build();
        }
    }
}