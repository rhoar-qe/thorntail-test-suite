package org.wildfly.swarm.ts.javaee.jaxrs.jsonb;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class HelloResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String hello() {
        Result result = new Result();
        result.hello = "world";

        Jsonb jsonb = JsonbBuilder.create();
        return jsonb.toJson(result);
    }
}
