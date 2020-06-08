package org.wildfly.swarm.ts.microprofile.rest.client.v14.clientheaders;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

@Path("/")
public class SimpleResource {
    @Context
    HttpHeaders headers;

    @GET
    @Path("/headers")
    public JsonObject getHeaders() {
        final JsonObjectBuilder builder = Json.createObjectBuilder();
        headers.getRequestHeaders().forEach((key, value) -> builder.add(key, String.valueOf(value.get(0))));
        return builder.build();
    }
}
