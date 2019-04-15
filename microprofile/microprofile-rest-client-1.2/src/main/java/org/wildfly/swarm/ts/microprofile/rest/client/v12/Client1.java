package org.wildfly.swarm.ts.microprofile.rest.client.v12;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

// since 1.2 New baseUri property added to @RegisterRestClient annotation.
@RegisterRestClient(baseUri = "http://localhost:8080")
public interface Client1 {
    @GET
    @Path("/rest/simple1")
    String simpleOperation1();
}
