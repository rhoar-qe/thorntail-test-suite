package org.wildfly.swarm.ts.microprofile.opentracing.v13;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/")
public class Resource {
    @Inject
    private TracedService service;

    @GET
    @Path("/tracedlogged")
    public Response tracedLoggedMethod() {
        return Response.ok().entity(service.tracedLoggedMethod()).build();
    }

    @GET
    @Path("/traced")
    public Response tracedMethod() {
        return Response.ok().entity(service.tracedMethod()).build();
    }

    @GET
    @Path("/logged")
    public Response loggedMethod() {
        return Response.ok().entity(service.loggedMethod()).build();
    }

    @GET
    @Path("/nottracednotlogged")
    public Response notTracedNotLoggedMethod() {
        return Response.ok().entity(service.notTracedNotLoggedMethod()).build();
    }

    @GET
    @Path("/named/true")
    public Response tracedNamedTrue() {
        return Response.ok().entity(service.tracedNamedTrue()).build();
    }

    @GET
    @Path("/true")
    public Response tracedTrue() {
        return Response.ok().entity(service.tracedTrue()).build();
    }

    @GET
    @Path("/named/false")
    public Response tracedNamedFalse() {
        return Response.ok().entity(service.tracedNamedFalse()).build();
    }

    @GET
    @Path("/false")
    public Response tracedFalse() {
        return Response.ok().entity(service.tracedFalse()).build();
    }

    // Clarify http-path when path contains regular expressions (#136: https://github.com/eclipse/microprofile-opentracing/pull/136)
    @GET
    @Path("/test/{id: \\d+}/{txt: \\w+}")
    public Response twoWildcard(@PathParam("id") long id, @PathParam("txt") String txt) {
        return Response.ok().entity(service.twoWildcard(id, txt)).build();
    }
}
