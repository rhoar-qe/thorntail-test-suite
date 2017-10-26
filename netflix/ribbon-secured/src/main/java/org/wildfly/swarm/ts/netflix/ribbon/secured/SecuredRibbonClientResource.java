package org.wildfly.swarm.ts.netflix.ribbon.secured;

import io.netty.buffer.ByteBuf;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.nio.charset.StandardCharsets;

@Path("/test")
public class SecuredRibbonClientResource {
    @GET
    public Response test() {
        ByteBuf response = SecuredRibbonClient.INSTANCE.ribbonTest().execute();
        return Response.ok().entity(response.toString(StandardCharsets.UTF_8)).build();
    }
}
