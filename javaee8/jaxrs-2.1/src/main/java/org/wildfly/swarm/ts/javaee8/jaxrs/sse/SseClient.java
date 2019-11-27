package org.wildfly.swarm.ts.javaee8.jaxrs.sse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.sse.SseEventSource;

@Path("/client")
public class SseClient {
    @GET
    @Path("/simple")
    public String getSimpleEvent() {
        return getSse("http://localhost:8080/simple");
    }

    @GET
    @Path("/broadcast")
    public String getBroadcast() {
        return getSse("http://localhost:8080/broadcast");
    }

    private static String getSse(String url) {
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target(url);
        try (SseEventSource source = SseEventSource.target(target).build()) {
            source.open();
            return target.request().get(String.class);
        }
    }
}
