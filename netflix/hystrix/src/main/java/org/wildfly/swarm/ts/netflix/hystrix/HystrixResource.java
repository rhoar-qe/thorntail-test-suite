package org.wildfly.swarm.ts.netflix.hystrix;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.netflix.hystrix.HystrixCommand;

@Path("/")
public class HystrixResource {

    private static AtomicBoolean throwError = new AtomicBoolean(false);

    @GET
    public Response get() {
        HystrixCommand<String> command = new TestHystrixCommand(throwError.get());
        String response = command.execute(); 
        return Response.ok().entity(response).build();
     }

    @POST
    @Path("/throwError")
    public void disable() {
        throwError.set(true);
    }
}
