package org.wildfly.swarm.ts.microprofile;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class HelloResource {
    @Inject
    private HelloService hello;

    @GET
    public JsonObject hello() {
        return Json.createObjectBuilder()
                .add("content", hello.hello())
                .build();
    }
}
