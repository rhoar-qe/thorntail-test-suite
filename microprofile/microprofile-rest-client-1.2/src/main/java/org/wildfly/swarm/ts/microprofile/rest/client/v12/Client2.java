package org.wildfly.swarm.ts.microprofile.rest.client.v12;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

// baseUri will be overridden by microprofile-config.properties
// org.wildfly.swarm.ts.microprofile.rest.client.v12.Client2/mp-rest/url=http://localhost:8080
@RegisterRestClient(baseUri = "http://localhost:2123")
public interface Client2 {
    @GET
    @Path("/rest/simple2")
    String simpleOperation2();
}
