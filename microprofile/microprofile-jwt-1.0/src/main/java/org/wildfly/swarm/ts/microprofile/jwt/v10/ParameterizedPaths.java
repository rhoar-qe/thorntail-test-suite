package org.wildfly.swarm.ts.microprofile.jwt.v10;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/parameterized-paths")
public class ParameterizedPaths {
    @GET
    @Path("/my/{path}/admin")
    @RolesAllowed("admin")
    public String admin(@PathParam("path") String path) {
        return "Admin accessed " + path;
    }

    @GET
    @Path("/my/{path}/view")
    @RolesAllowed("view")
    public String view(@PathParam("path") String path) {
        return "View accessed " + path;
    }
}
