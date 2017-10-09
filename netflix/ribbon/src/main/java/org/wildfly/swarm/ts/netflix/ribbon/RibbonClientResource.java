package org.wildfly.swarm.ts.netflix.ribbon;

import java.nio.charset.StandardCharsets;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import io.netty.buffer.ByteBuf;
import rx.Observable;

@Path("/")
public class RibbonClientResource {

    @GET
    @Path("test")
    public Response test() {
        Observable<ByteBuf > obs = RibbonClient.INSTANCE.ribbonTest().observe();
        return Response.ok().entity(obs.toBlocking().single().toString(StandardCharsets.UTF_8)).build();
    }
}
