package org.wildfly.swarm.ts.fraction.autodetection.cdi.jaxrs.ejb;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Stateless
@Path("/")
public class HelloResource {
    @Inject
    private HelloService hello;

    @GET
    public Response get(@QueryParam("name") String name) {
        return Response.ok().entity("JAX-RS says: " + hello.hello(name)).build();
    }
}
