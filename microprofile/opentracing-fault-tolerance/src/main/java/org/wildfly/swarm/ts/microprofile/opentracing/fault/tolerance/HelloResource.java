package org.wildfly.swarm.ts.microprofile.opentracing.fault.tolerance;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

@Path("/")
public class HelloResource {
    @Inject
    private MyService myService;

    @GET
    @Path("/hello")
    public String get() {
        myService.reset();
        return myService.hello();
    }

    @GET
    @Path("/helloAsync")
    public CompletionStage<String> getAsync() {
        myService.reset();
        return myService.helloAsync();
    }
}
