package org.wildfly.swarm.ts.microprofile.restclient.v10;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient
public interface RestSimpleResourceRestClient {

    @GET
    @Path("/rest/simple")
    public String simpleOperation();
}
