package org.wildfly.swarm.ts.microprofile.rest.client.v12;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

// baseUri will be overridden by microprofile-config.properties
// org.wildfly.swarm.ts.microprofile.rest.client.v12.RestClient2/mp-rest/url=http://localhost:8080
@RegisterRestClient(baseUri = "http://localhost:2123")
public interface RestClient2 {
    @GET
    @Path("/rest/simple2")
    String simpleOperation2();
}
