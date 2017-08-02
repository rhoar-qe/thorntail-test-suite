package org.wildfly.swarm.ts.wildfly.topology.webapp;

import org.wildfly.swarm.topology.AdvertisementHandle;
import org.wildfly.swarm.topology.Topology;

import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.naming.NamingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Path("/topology")
public class CurrentTopologyResource {
    private static final Map<String, AdvertisementHandle> advertisements = new ConcurrentHashMap<>();

    @PUT
    @Path("/{service}")
    public Response advertise(@PathParam("service") String service) throws NamingException {
        // racy, but OK in this test
        if (advertisements.containsKey(service)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        AdvertisementHandle handle = Topology.lookup().advertise(service);
        advertisements.put(service, handle);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{service}")
    public Response unadvertise(@PathParam("service") String service) {
        // racy, but OK in this test
        if (!advertisements.containsKey(service)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        advertisements.remove(service).unadvertise();
        return Response.ok().build();
    }
}
