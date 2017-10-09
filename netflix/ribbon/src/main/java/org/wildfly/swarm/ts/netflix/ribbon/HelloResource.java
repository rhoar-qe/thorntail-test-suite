package org.wildfly.swarm.ts.netflix.ribbon;

import org.wildfly.swarm.topology.Topology;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/")
public class HelloResource {

    private AtomicBoolean enabled = new AtomicBoolean(true);

    @GET
    public Response get() {
        if (enabled.get()) {
            try {
                String response = Topology.lookup().asMap().keySet().toString();
                return Response.ok().entity(response).build();
            } catch (Exception e) {
                return Response.status(500).build();
            }
        } else {
            return Response.status(404).build();
        }
    }

    @GET
    @Path("disable")
    public void disable() {
        enabled.set(false);
    }
}
