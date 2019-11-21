package org.wildfly.swarm.ts.javaee8.security.auth.basic;

import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class SimpleResource {
    @Inject
    private SecurityContext securityContext;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response get() {
        return Response.ok().entity("GET by user " + securityContext.getCallerPrincipal().getName() + " in role " +
                (securityContext.isCallerInRole("ADMIN") ? "ADMIN" : "USER")).build();
    }

    @POST
    @Path("/{value}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response set(@PathParam("value") String value) {
        return Response.ok().entity("POST " + value + " by user " + securityContext.getCallerPrincipal().getName() +
                " in role ADMIN:" + securityContext.isCallerInRole("ADMIN")).build();
    }
}
