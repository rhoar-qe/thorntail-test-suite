package org.wildfly.swarm.ts.wildfly.topology.webapp;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/service")
public class ExampleServiceResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject get(@QueryParam("input") String input) {
        return Json.createObjectBuilder()
                .add("hello", "Hello, " + input + "!")
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject post(JsonObject message) {
        String input = message.getString("input");
        return Json.createObjectBuilder()
                .add("hello", "Hi there, " + input + "!")
                .build();
    }
}
