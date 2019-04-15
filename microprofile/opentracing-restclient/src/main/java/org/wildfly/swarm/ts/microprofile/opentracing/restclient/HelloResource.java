package org.wildfly.swarm.ts.microprofile.opentracing.restclient;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/hello")
public class HelloResource {
    @Inject
    private MyService myService;

    @GET
    public String get() {
        return myService.hello();
    }
}
