package org.wildfly.swarm.ts.microprofile.rest.client.v13.mediatype;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Function;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.RestClientBuilder;

@Path("/")
public class RestClientResource {
    // @Produces

    @GET
    @Path("/producesText_withoutAnnotation")
    public Response producesText_withoutAnnotation() {
        try {
            return response(ClientMediaType::producesText_withoutAnnotation);
        } catch (Exception e) {
            return errorResponse(e);
        }
    }

    @GET
    @Path("/producesJson_withoutAnnotation")
    public Response producesJson_withoutAnnotation() {
        try {
            return response(ClientMediaType::producesJson_withoutAnnotation);
        } catch (Exception e) {
            return errorResponse(e);
        }
    }

    @GET
    @Path("/producesXml_withoutAnnotation")
    public Response producesXml_withoutAnnotation() {
        try {
            return response(ClientMediaType::producesXml_withoutAnnotation);
        } catch (Exception e) {
            return errorResponse(e);
        }
    }

    @GET
    @Path("/producesText_defaultAnnotationValue")
    public Response producesText_defaultAnnotationValue() {
        try {
            return response(ClientMediaType::producesText_defaultAnnotationValue);
        } catch (Exception e) {
            return errorResponse(e);
        }
    }

    @GET
    @Path("/producesJson_defaultAnnotationValue")
    public Response producesJson_defaultAnnotationValue() {
        try {
            return response(ClientMediaType::producesJson_defaultAnnotationValue);
        } catch (Exception e) {
            return errorResponse(e);
        }
    }

    @GET
    @Path("/producesXml_defaultAnnotationValue")
    public Response producesXml_defaultAnnotationValue() {
        try {
            return response(ClientMediaType::producesXml_defaultAnnotationValue);
        } catch (Exception e) {
            return errorResponse(e);
        }
    }

    @GET
    @Path("/producesText_annotationValueTextPlain")
    public Response producesText_annotationValueTextPlain() {
        try {
            return response(ClientMediaType::producesText_annotationValueTextPlain);
        } catch (Exception e) {
            return errorResponse(e);
        }
    }

    @GET
    @Path("/producesJson_annotationValueTextPlain")
    public Response producesJson_annotationValueTextPlain() {
        try {
            return response(ClientMediaType::producesJson_annotationValueTextPlain);
        } catch (Exception e) {
            return errorResponse(e);
        }
    }

    // @Consumes

    @GET
    @Path("/consumesText_withoutAnnotation")
    public Response consumesText_withoutAnnotation() {
        try {
            return response(client -> client.consumesText_withoutAnnotation(ResourceMediaType.GREETINGS_TEXT));
        } catch (Exception e) {
            return errorResponse(e);
        }
    }

    @GET
    @Path("/consumesJson_withoutAnnotation")
    public Response consumesJson_withoutAnnotation() {
        try {
            return response(client -> client.consumesJson_withoutAnnotation(ResourceMediaType.GREETINGS_JSON));
        } catch (Exception e) {
            return errorResponse(e);
        }
    }

    @GET
    @Path("/consumesText_defaultAnnotationValue")
    public Response consumesText_defaultAnnotationValue() {
        try {
            return response(client -> client.consumesText_defaultAnnotationValue(ResourceMediaType.GREETINGS_TEXT));
        } catch (Exception e) {
            return errorResponse(e);
        }
    }

    @GET
    @Path("/consumesText_annotationValueTextPlain")
    public Response consumesText_annotationValueTextPlain() {
        try {
            return response(client -> client.consumesText_annotationValueTextPlain(ResourceMediaType.GREETINGS_TEXT));
        } catch (Exception e) {
            return errorResponse(e);
        }
    }

    // ---

    private Response response(Function<ClientMediaType, String> call) throws MalformedURLException {
        ClientMediaType client = RestClientBuilder.newBuilder()
                .baseUrl(new URL("http://localhost:8080"))
                .build(ClientMediaType.class);
        String result = call.apply(client);
        return Response.ok().entity(result).build();
    }

    private Response errorResponse(Exception e) {
        return Response.serverError().entity(e.getMessage()).build();
    }
}
