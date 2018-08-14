package org.wildfly.swarm.ts.microprofile.openapi.v10;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

@Path("/rest")
public class RestAnnotatedResource {

    @GET
    @Path("/{pathParam}")
    @Operation(summary = "Annotated operation summary", operationId = "annotatedOp")
    @APIResponse(content=@Content(mediaType="application/json", schema=@Schema(implementation=ResponsePOJO.class)))
    public ResponsePOJO annotatedOperation(@PathParam("pathParam") String pathParam, @QueryParam("queryParam") String queryParam) {
        return new ResponsePOJO(pathParam, queryParam);
    }
}
