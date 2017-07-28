package org.wildfly.swarm.ts.javaee.jaxrs.jsonp;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class HelloResource {
    @GET
    public JsonObject hello() {
        return Json.createObjectBuilder()
                .add("hello", "world")
                .build();
    }
}
