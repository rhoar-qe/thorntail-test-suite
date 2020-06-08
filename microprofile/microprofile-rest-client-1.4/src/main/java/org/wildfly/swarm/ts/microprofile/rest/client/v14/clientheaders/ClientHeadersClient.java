package org.wildfly.swarm.ts.microprofile.rest.client.v14.clientheaders;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/")
@RegisterRestClient(baseUri = "http://localhost:8080")
@RegisterClientHeaders(ClientHeadersFactoryImpl.class)
public interface ClientHeadersClient {

    @GET
    @Path("/rest/headers")
    String getHeaders();
}
