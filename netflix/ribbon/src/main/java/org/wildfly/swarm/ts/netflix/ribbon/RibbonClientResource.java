package org.wildfly.swarm.ts.netflix.ribbon;

import io.netty.buffer.ByteBuf;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.nio.charset.StandardCharsets;

@Path("/test")
public class RibbonClientResource {
    @GET
    public Response test() {
        ByteBuf response = RibbonClient.INSTANCE.ribbonTest().execute();
        return Response.ok().entity(response.toString(StandardCharsets.UTF_8)).build();
    }
}
