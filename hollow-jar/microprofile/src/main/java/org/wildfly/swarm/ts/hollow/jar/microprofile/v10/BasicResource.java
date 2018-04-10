package org.wildfly.swarm.ts.hollow.jar.microprofile.v10;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/basic")
public class BasicResource {
    @Inject
    private BasicService hello;

    @GET
    public JsonObject hello() {
        return Json.createObjectBuilder()
                .add("content", hello.hello())
                .build();
    }
}
