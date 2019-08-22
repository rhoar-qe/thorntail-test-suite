package org.wildfly.swarm.ts.microprofile.rest.client.v12;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.net.URI;
import java.net.URL;

@Path("/client")
public class RestClientResource {
    private static final String HTTP_LOCALHOST_8080 = "http://localhost:8080";

    @Inject
    @RestClient
    private Client1 client1;

    @Inject
    @RestClient
    private Client2 client2;

    @Inject
    @RestClient
    private Client3 client3;

    @GET
    @Path("/injected1")
    public String injected1() {
        return client1.simpleOperation1();
    }

    @GET
    @Path("/programmatic1")
    public String programmatic1() throws Exception {
        Client1 programmaticRestClient = RestClientBuilder.newBuilder()
                .baseUrl(new URL(HTTP_LOCALHOST_8080))
                .build(Client1.class);
        return programmaticRestClient.simpleOperation1();
    }

    @GET
    @Path("/injected2")
    public String injected2() {
        return client2.simpleOperation2();
    }

    @GET
    @Path("/programmatic2")
    public String programmatic2() { // using CDI
        Client2 client = CDI.current().select(Client2.class, RestClient.LITERAL).get();
        return client.simpleOperation2();
    }

    @GET
    @Path("/injected3")
    public String injected3() {
        return client3.simpleOperation3();
    }

    @GET
    @Path("/programmatic3")
    public String programmatic3() throws Exception {
        Client3 client = RestClientBuilder.newBuilder()
                .baseUri(new URI(HTTP_LOCALHOST_8080)) // since 1.1 New baseUri method on RestClientBuilder
                .build(Client3.class);
        return client.simpleOperation3();
    }
}
