package org.wildfly.swarm.ts.microprofile.rest.client.v12;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

// since 1.2 New baseUri property added to @RegisterRestClient annotation.
@RegisterRestClient(baseUri = "http://localhost:8080")
public interface RestClient1 {
    @GET
    @Path("/rest/simple1")
    String simpleOperation1();
}
