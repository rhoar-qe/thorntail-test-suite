package org.wildfly.swarm.ts.microprofile.rest.client.v12.async;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.concurrent.CompletionStage;

@RegisterRestClient(baseUri = "http://localhost:8080")
public interface Client {
    @GET
    @Path("/rest/simple/hello/{name}")
    CompletionStage<String> helloAsync(@PathParam("name") String name);
}
