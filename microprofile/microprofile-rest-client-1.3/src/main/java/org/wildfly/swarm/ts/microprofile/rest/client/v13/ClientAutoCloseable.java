package org.wildfly.swarm.ts.microprofile.rest.client.v13;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@RegisterRestClient(configKey = "myClientsKey")
public interface ClientAutoCloseable extends AutoCloseable {
    @GET
    @Path("/rest/simple")
    String simpleOperation();
}
