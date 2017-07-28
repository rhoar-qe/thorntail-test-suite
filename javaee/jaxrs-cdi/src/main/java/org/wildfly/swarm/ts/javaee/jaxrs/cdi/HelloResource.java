package org.wildfly.swarm.ts.javaee.jaxrs.cdi;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class HelloResource {
    @Inject
    private HelloService hello;

    @GET
    public String hello() {
        return hello.hello();
    }
}
