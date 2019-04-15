package org.wildfly.swarm.ts.microprofile.opentracing.restclient;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@RegisterRestClient(baseUri = "http://localhost:8080")
public interface HelloClient {
    @GET
    @Path("/rest/hello")
    String get();
}
