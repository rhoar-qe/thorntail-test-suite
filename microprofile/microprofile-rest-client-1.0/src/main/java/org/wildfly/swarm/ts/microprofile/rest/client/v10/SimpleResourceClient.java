package org.wildfly.swarm.ts.microprofile.rest.client.v10;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@RegisterRestClient
public interface SimpleResourceClient {
    @GET
    @Path("/rest/simple")
    String simpleOperation();
}
