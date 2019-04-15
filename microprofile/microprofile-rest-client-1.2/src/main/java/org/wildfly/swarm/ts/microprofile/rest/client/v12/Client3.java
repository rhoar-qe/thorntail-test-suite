package org.wildfly.swarm.ts.microprofile.rest.client.v12;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

// baseUri will be overridden by microprofile-config.properties
// org.wildfly.swarm.ts.microprofile.rest.client.v12.Client3/mp-rest/url=http://localhost:1234
// and than overridden by URI (that supersedes URL)
// org.wildfly.swarm.ts.microprofile.rest.client.v12.Client3/mp-rest/uri=http://localhost:8080
@RegisterRestClient(baseUri = "http://localhost:3212")
public interface Client3 {
    @GET
    @Path("/rest/simple3")
    String simpleOperation3();
}
