package org.wildfly.swarm.ts.microprofile.rest.client.v14.clientheaders;

import java.net.URL;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.eclipse.microprofile.rest.client.RestClientBuilder;

@Path("/")
public class ClientResource {

    @GET
    @Path("/client")
    public String headers() throws Exception {
        ClientHeadersClient client = RestClientBuilder.newBuilder()
                .baseUrl(new URL("http://localhost:8080"))
                .build(ClientHeadersClient.class);
        return client.getHeaders();
    }
}
