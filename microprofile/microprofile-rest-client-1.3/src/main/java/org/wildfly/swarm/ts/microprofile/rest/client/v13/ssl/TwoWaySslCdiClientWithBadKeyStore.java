package org.wildfly.swarm.ts.microprofile.rest.client.v13.ssl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "TwoWaySslCdiClientWithBadKeyStore")
public interface TwoWaySslCdiClientWithBadKeyStore {
    @GET
    @Path("/simple")
    String get();
}
