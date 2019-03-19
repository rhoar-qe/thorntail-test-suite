package org.wildfly.swarm.ts.microprofile.rest.client.v12;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

// baseUri will be overridden by microprofile-config.properties
// org.wildfly.swarm.ts.microprofile.rest.client.v12.RestClient3/mp-rest/url=http://localhost:1234
// and than overridden by URI (that supersedes URL)
// org.wildfly.swarm.ts.microprofile.rest.client.v12.RestClient3/mp-rest/uri=http://localhost:8080
@RegisterRestClient(baseUri = "http://localhost:3212")
public interface RestClient3 {
    @GET
    @Path("/rest/simple3")
    String simpleOperation3();
}
