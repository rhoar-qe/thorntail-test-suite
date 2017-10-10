package org.wildfly.swarm.ts.netflix.ribbon;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.concurrent.atomic.AtomicBoolean;

@Path("/hello")
public class HelloResource {
    private static AtomicBoolean enabled = new AtomicBoolean(true);

    @GET
    public Response get() {
        if (enabled.get()) {
            return Response.ok().entity("Hello, World!").build();
        } else {
            return Response.serverError().build();
        }
    }

    @POST
    @Path("/disable")
    public void disable() {
        enabled.set(false);
    }
}
