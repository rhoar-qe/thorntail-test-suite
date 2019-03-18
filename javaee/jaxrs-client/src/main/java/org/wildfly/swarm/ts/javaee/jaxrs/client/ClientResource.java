package org.wildfly.swarm.ts.javaee.jaxrs.client;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

@Path("/client")
public class ClientResource {
    @GET
    public String get() {
        Client client = ClientBuilder.newClient();
        try {
            String response = client.target("http://localhost:8080/hello").request().get(String.class);
            return "JAX-RS Client got: " + response;
        } finally {
            client.close();
        }
    }
}
