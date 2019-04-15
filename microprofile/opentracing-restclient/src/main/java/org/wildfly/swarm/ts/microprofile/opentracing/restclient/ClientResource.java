package org.wildfly.swarm.ts.microprofile.opentracing.restclient;

import org.eclipse.microprofile.opentracing.ClientTracingRegistrar;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.net.MalformedURLException;
import java.net.URL;

@Path("/client")
public class ClientResource {
    @Inject
    @RestClient
    private HelloClient hello;

    @GET
    @Path("/injected")
    public String injected() {
        return hello.get();
    }

    @GET
    @Path("/programmatic")
    public String programmatic() throws MalformedURLException {
        HelloClient client = RestClientBuilder.newBuilder()
                .baseUrl(new URL("http://localhost:8080"))
                .build(HelloClient.class);
        return client.get();
    }

    @GET
    @Path("/jaxrs")
    public String jaxrs() {
        Client client = ClientTracingRegistrar.configure(ClientBuilder.newBuilder()).build();
        try {
            return client.target("http://localhost:8080")
                    .path("/rest/hello")
                    .request()
                    .get()
                    .readEntity(String.class);
        } finally {
            client.close();
        }
    }
}
