package org.wildfly.swarm.ts.microprofile.openapi.v11;

import javax.ws.rs.PATCH;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.extensions.Extension;
import org.eclipse.microprofile.openapi.annotations.extensions.Extensions;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

@Path("/rest")
public class RestOpenApi11Resource {
    @PATCH
    @Path("/patch")
    @APIResponse(content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = String.class), example = "content-example"))
    @Extensions({
            @Extension(name = "x-string-property", value = "string-value"),
            @Extension(name = "x-boolean-property", value = "true", parseValue = true),
            @Extension(name = "x-number-property", value = "42", parseValue = true),
            @Extension(
                    name = "x-object-property",
                    value = "{ \"property-1\" : \"value-1\", \"property-2\" : \"value-2\", \"property-3\" : { \"prop-3-1\" : 42, \"prop-3-2\" : true } }",
                    parseValue = true),
            @Extension(name = "x-string-array-property", value = "[ \"one\", \"two\", \"three\" ]", parseValue = true),
            @Extension(name = "x-object-array-property", value = "[ { \"name\": \"item-1\" }, { \"name\" : \"item-2\" } ]", parseValue = true)
    })
    public Response annotatedOperation() {
        return Response.ok().entity("Patch OK").build();
    }
}
