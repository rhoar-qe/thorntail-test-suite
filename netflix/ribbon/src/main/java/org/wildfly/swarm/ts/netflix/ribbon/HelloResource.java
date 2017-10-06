package org.wildfly.swarm.ts.netflix.ribbon;

import org.wildfly.swarm.topology.Topology;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/")
public class HelloResource {
    @GET
    public Response get() {
        try {
            String response = Topology.lookup().asMap().keySet().toString();
            return Response.ok().entity(response).build();
        } catch (Exception e) {
            return Response.status(500).build();
        }
    }
}
