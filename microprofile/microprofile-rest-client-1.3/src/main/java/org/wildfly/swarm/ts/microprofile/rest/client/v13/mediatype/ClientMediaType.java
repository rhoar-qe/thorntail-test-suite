package org.wildfly.swarm.ts.microprofile.rest.client.v13.mediatype;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(baseUri = "http://localhost:8080")
public interface ClientMediaType {
    // @Produces

    @GET
    @Path("/rest/greetings-text")
    String producesText_withoutAnnotation();

    @GET
    @Path("/rest/greetings-json")
    String producesJson_withoutAnnotation();

    @GET
    @Path("/rest/greetings-xml")
    String producesXml_withoutAnnotation();

    @GET
    @Produces
    @Path("/rest/greetings-text")
    String producesText_defaultAnnotationValue();

    @GET
    @Produces
    @Path("/rest/greetings-json")
    String producesJson_defaultAnnotationValue();

    @GET
    @Produces
    @Path("/rest/greetings-xml")
    String producesXml_defaultAnnotationValue();

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/rest/greetings-text")
    String producesText_annotationValueTextPlain();

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/rest/greetings-json")
    String producesJson_annotationValueTextPlain();

    // @Consumes

    @POST
    @Path("/rest/consume-text")
    String consumesText_withoutAnnotation(String text);

    @POST
    @Path("/rest/consume-json")
    String consumesJson_withoutAnnotation(String text);

    @POST
    @Consumes
    @Path("/rest/consume-text")
    String consumesText_defaultAnnotationValue(String text);

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("/rest/consume-text")
    String consumesText_annotationValueTextPlain(String text);
}
