package org.wildfly.swarm.ts.javaee.jaxrs.client.json;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

@Path("/client")
public class ClientResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Hello get() {
        Client client = ClientBuilder.newClient();
        try {
            Hello response = client.target("http://localhost:8080/hello").request().get(Hello.class);
            return new Hello("JAX-RS Client got: " + response.value);
        } finally {
            client.close();
        }
    }
}
