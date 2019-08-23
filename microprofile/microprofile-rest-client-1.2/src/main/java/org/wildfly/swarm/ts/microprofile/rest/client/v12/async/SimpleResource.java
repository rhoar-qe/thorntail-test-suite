package org.wildfly.swarm.ts.microprofile.rest.client.v12.async;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Path("/simple")
public class SimpleResource {
    @GET
    @Path("/hello/{name}")
    public CompletionStage<String> helloAsync(@PathParam("name") String name) {
        return CompletableFuture.completedFuture("Hello '" + name + "' from async");
    }
}
