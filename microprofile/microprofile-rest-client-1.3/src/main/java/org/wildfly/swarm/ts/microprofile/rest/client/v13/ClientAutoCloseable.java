package org.wildfly.swarm.ts.microprofile.rest.client.v13;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "myClientsKey")
public interface ClientAutoCloseable extends AutoCloseable {
    @GET
    @Path("/rest/simple")
    String simpleOperation();
}
