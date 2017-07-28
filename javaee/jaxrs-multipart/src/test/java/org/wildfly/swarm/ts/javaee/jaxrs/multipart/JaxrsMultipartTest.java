package org.wildfly.swarm.ts.javaee.jaxrs.multipart;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.util.EntityUtils;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment
public class JaxrsMultipartTest {
    @Test
    @RunAsClient
    public void post_multipartRequest() throws IOException {
        String response = Request.Post("http://localhost:8080/").body(
                MultipartEntityBuilder.create()
                        .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                        .addTextBody("text", "MULTIPART!", ContentType.DEFAULT_TEXT)
                        .addBinaryBody("binary", new byte[]{72, 69, 76, 76, 79}, ContentType.DEFAULT_BINARY, "upload.txt")
                        .build()
        ).execute().returnContent().asString();

        assertThat(response).isEqualTo("OK: MULTIPART! HELLO");
    }

    @Test
    @RunAsClient
    public void get_multipartResponse() throws IOException {
        HttpResponse response = Request.Get("http://localhost:8080/").execute().returnResponse();

        String contentType = response.getEntity().getContentType().getValue();
        assertThat(contentType).startsWith("multipart/form-data");
        String boundary = "--" + contentType.substring("multipart/form-data; boundary=".length());

        String actualResponse = EntityUtils.toString(response.getEntity()).replace("\r\n", "\n");

        // this is fragile, but will do for now
        String expectedResponse = ""
                + boundary + "\n"
                + "Content-Disposition: form-data; name=\"text\"\n"
                + "Content-Type: text/plain\n"
                + "\n"
                + "Hello, World!\n"
                + boundary + "\n"
                + "Content-Disposition: form-data; name=\"binary\"\n"
                + "Content-Type: application/octet-stream\n"
                + "\n"
                + "MULTIPART!\n"
                + boundary + "--";

        assertThat(actualResponse).isEqualTo(expectedResponse);
    }
}
