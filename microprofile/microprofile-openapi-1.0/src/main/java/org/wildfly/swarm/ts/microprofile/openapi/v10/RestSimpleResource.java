package org.wildfly.swarm.ts.microprofile.openapi.v10;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("/rest")
public class RestSimpleResource {
    @GET
    @Path("/{pathParam}")
    public Response simpleOperation(@PathParam("pathParam") String pathParam, @QueryParam("queryParam") String queryParam) {
        return Response.ok().entity("SimpleResource " + pathParam + " " + queryParam).build();
    }
}
