package org.wildfly.swarm.ts.microprofile.rest.client.v12.async;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.concurrent.CompletionStage;

@Path("/client")
public class RestClientResource {
    @Inject
    @RestClient
    private Client client;

    @GET
    @Path("/hello")
    public CompletionStage<String> pathParam() {
        return client.helloAsync("Bob");
    }
}
