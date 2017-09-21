package org.wildfly.swarm.ts.netflix.ribbon;

import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.wildfly.swarm.topology.Topology;

@Path("/")
public class HelloResource {
    @GET
    public Response get() {
        String response = "";

        Topology topology;
        try {
            topology = Topology.lookup();
            Map<String, List<Topology.Entry>> entries = topology.asMap();
            for (String key : entries.keySet()) {               
                response = response + key;
            }
        } catch (Exception e) {
            return Response.status(500).build();
        }

        return Response.ok().entity(response).build();
    }
}
