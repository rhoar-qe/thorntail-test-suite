package org.wildfly.swarm.ts.javaee.jaxrs.jsonb;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/adapt")
public class AdapterResource {
    private static final Jsonb jsonb = JsonbBuilder.create(
            new JsonbConfig().withAdapters(new ResultAdapter())
    );

    @GET
    @Path("/to-json")
    @Produces(MediaType.APPLICATION_JSON)
    public String adaptToJson() {
        Result result = new Result();
        result.hello = "world";
        return jsonb.toJson(result);
    }

    @GET
    @Path("/from-json")
    @Produces(MediaType.APPLICATION_JSON)
    public String adaptFromJson() {
        return jsonb.fromJson("{\"hello\":\"world\"}", Result.class).hello;
    }
}
