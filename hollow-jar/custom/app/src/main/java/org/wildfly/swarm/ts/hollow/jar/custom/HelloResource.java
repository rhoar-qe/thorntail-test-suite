package org.wildfly.swarm.ts.hollow.jar.custom;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("/hello")
public class HelloResource {
    @Inject
    private HelloService hello;

    @GET
    public Response get(@QueryParam("name") @DefaultValue("<unspecified>") String name) {
        return Response.ok().entity("JAX-RS says: " + hello.hello(name)).build();
    }
}
