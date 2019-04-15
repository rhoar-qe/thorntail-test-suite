package org.wildfly.swarm.ts.microprofile.rest.client.v10;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.net.URL;

@Path("/client")
public class ClientResource {
    @Inject
    @RestClient
    private SimpleResourceClient restClient;

    @GET
    @Path("/injected")
    public String injected() {
        return restClient.simpleOperation();
    }

    @GET
    @Path("/programatic")
    public String programatic() throws Exception {
        SimpleResourceClient programaticRestClient = RestClientBuilder.newBuilder()
                .baseUrl(new URL("http://localhost:8080"))
                .build(SimpleResourceClient.class);
        return programaticRestClient.simpleOperation();
    }
}
