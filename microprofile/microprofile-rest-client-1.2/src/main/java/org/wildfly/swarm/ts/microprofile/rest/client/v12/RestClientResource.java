package org.wildfly.swarm.ts.microprofile.rest.client.v12;

import java.net.URI;
import java.net.URL;

import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/client")
public class RestClientResource {
    private static final String HTTP_LOCALHOST_8080 = "http://localhost:8080";

    @Inject
    @RestClient
    private RestClient1 restClient1;

    @Inject
    @RestClient
    private RestClient2 restClient2;

    @Inject
    @RestClient
    private RestClient3 restClient3;

    @GET
    @Path("/injected1")
    public String injected() {
        return restClient1.simpleOperation1();
    }

    @GET
    @Path("/programatic1")
    public String programatic() throws Exception {
        RestClient1 programaticRestClient = RestClientBuilder.newBuilder()
                .baseUrl(new URL(HTTP_LOCALHOST_8080))
                .build(RestClient1.class);
        return programaticRestClient.simpleOperation1();
    }

    @GET
    @Path("/injected2")
    public String injected2() {
        return restClient2.simpleOperation2();
    }

    @GET
    @Path("/programatic2")
    public String programatic2() { // using CDI
        RestClient2 client = CDI.current().select(RestClient2.class,
                                                  RestClient.LITERAL).get();
        return client.simpleOperation2();
    }

    @GET
    @Path("/injected3")
    public String injected3() {
        return restClient3.simpleOperation3();
    }

    @GET
    @Path("/programatic3")
    public String programatic3() throws Exception {
        RestClient3 client = RestClientBuilder.newBuilder()
                .baseUri(new URI(HTTP_LOCALHOST_8080)) // since 1.1 New baseUri method on RestClientBuilder
                .build(RestClient3.class);
        return client.simpleOperation3();
    }
}
