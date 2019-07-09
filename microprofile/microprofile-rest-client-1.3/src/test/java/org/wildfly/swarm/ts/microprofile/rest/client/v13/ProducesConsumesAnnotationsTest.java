package org.wildfly.swarm.ts.microprofile.rest.client.v13;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;
import org.wildfly.swarm.ts.microprofile.rest.client.v13.mediatype.ResourceMediaType;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment
public class ProducesConsumesAnnotationsTest {
    private static final String HTTP_REST = "http://localhost:8080/rest/";

    private static final String EXPECTED_ERROR_406 = "Unknown error, status code 406";

    private static final String EXPECTED_ERROR_415 = "Unknown error, status code 415";

    // @Produces

    // client without @Produces and resource with @Produces(MediaType.TEXT_PLAIN)
    @Test
    @RunAsClient
    public void producesText_withoutAnnotation() throws IOException {
        HttpResponse response = Request.Get(HTTP_REST + "producesText_withoutAnnotation").execute().returnResponse();
        checkResponse(response, 500, EXPECTED_ERROR_406);
    }

    // client without @Produces and resource with @Produces(MediaType.APPLICATION_JSON)
    @Test
    @RunAsClient
    public void producesJson_withoutAnnotation() throws IOException {
        HttpResponse response = Request.Get(HTTP_REST + "producesJson_withoutAnnotation").execute().returnResponse();
        checkResponse(response, 200, ResourceMediaType.GREETINGS_JSON);
    }

    // client without @Produces and resource with @Produces(MediaType.APPLICATION_XML)
    @Test
    @RunAsClient
    public void producesXml_withoutAnnotation() throws IOException {
        HttpResponse response = Request.Get(HTTP_REST + "producesXml_withoutAnnotation").execute().returnResponse();
        checkResponse(response, 500, EXPECTED_ERROR_406);
    }

    // client with @Produces() and resource with @Produces(MediaType.TEXT_PLAIN)
    @Test
    @RunAsClient
    public void producesText_defaultAnnotationValue() throws IOException {
        HttpResponse response = Request.Get(HTTP_REST + "producesText_defaultAnnotationValue").execute().returnResponse();
        checkResponse(response, 200, ResourceMediaType.GREETINGS_TEXT);
    }

    // client with @Produces() and resource with @Produces(MediaType.APPLICATION_JSON)
    @Test
    @RunAsClient
    public void producesJson_defaultAnnotationValue() throws IOException {
        HttpResponse response = Request.Get(HTTP_REST + "producesJson_defaultAnnotationValue").execute().returnResponse();
        checkResponse(response, 200, ResourceMediaType.GREETINGS_JSON);
    }

    // client with @Produces() and resource with @Produces(MediaType.APPLICATION_XML)
    @Test
    @RunAsClient
    public void producesXml_defaultAnnotationValue() throws IOException {
        HttpResponse response = Request.Get(HTTP_REST + "producesXml_defaultAnnotationValue").execute().returnResponse();
        checkResponse(response, 200, ResourceMediaType.GREETINGS_XML);
    }


    // client with @Produces(MediaType.TEXT_PLAIN) and resource with @Produces(MediaType.TEXT_PLAIN)
    @Test
    @RunAsClient
    public void producesText_annotationValueTextPlain() throws IOException {
        HttpResponse response = Request.Get(HTTP_REST + "producesText_annotationValueTextPlain").execute().returnResponse();
        checkResponse(response, 200, ResourceMediaType.GREETINGS_TEXT);
    }

    // client with @Produces(MediaType.TEXT_PLAIN) and resource with @Produces(MediaType.APPLICATION_JSON)
    @Test
    @RunAsClient
    public void producesJson_annotationValueTextPlain() throws IOException {
        HttpResponse response = Request.Get(HTTP_REST + "producesJson_annotationValueTextPlain").execute().returnResponse();
        checkResponse(response, 500, EXPECTED_ERROR_406);
    }

    // @Consumes

    // client without @Consumes and resource with @Consumes(MediaType.TEXT_PLAIN)
    @Test
    @RunAsClient
    public void consumesText_withoutAnnotation() throws IOException {
        HttpResponse response = Request.Get(HTTP_REST + "consumesText_withoutAnnotation").execute().returnResponse();
        checkResponse(response, 500, EXPECTED_ERROR_415);
    }

    // client without @Consumes and resource with @Consumes(MediaType.APPLICATION_JSON)
    @Test
    @RunAsClient
    public void consumesJson_withoutAnnotation() throws IOException {
        HttpResponse response = Request.Get(HTTP_REST + "consumesJson_withoutAnnotation").execute().returnResponse();
        checkResponse(response, 200, ResourceMediaType.GREETINGS_JSON);
    }

    // client with @Consumes() and resource with @Consumes(MediaType.TEXT_PLAIN)
    @Test
    @RunAsClient
    public void consumesText_defaultAnnotationValue() throws IOException {
        HttpResponse response = Request.Get(HTTP_REST + "consumesText_defaultAnnotationValue").execute().returnResponse();
        checkResponse(response, 200, ResourceMediaType.GREETINGS_TEXT);
    }

    // client with @Consumes(MediaType.TEXT_PLAIN) and resource with @Consumes(MediaType.TEXT_PLAIN)
    @Test
    @RunAsClient
    public void consumesText_annotationValueTextPlain() throws IOException {
        HttpResponse response = Request.Get(HTTP_REST + "consumesText_annotationValueTextPlain").execute().returnResponse();
        checkResponse(response, 200, ResourceMediaType.GREETINGS_TEXT);
    }

    private void checkResponse(HttpResponse response, int expectedStatusCode, String expectedContent) throws IOException {
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(expectedStatusCode);

        String actualContent = EntityUtils.toString(response.getEntity(), "UTF-8");
        assertThat(actualContent).isEqualTo(expectedContent);
    }
}
