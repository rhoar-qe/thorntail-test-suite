package org.wildfly.swarm.ts.javaee8.servlet;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.net.http.HttpClient.Version.HTTP_2;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment
public class MappingServletTest {
    @Test
    @RunAsClient
    public void contextRootMatch() throws IOException, InterruptedException {
        String response = sendRequest("");
        String expected = expectedResponse("CONTEXT_ROOT", "", "");
        assertThat(response).isEqualTo(expected);
    }

    @Test
    @RunAsClient
    public void defaultMatch() throws IOException, InterruptedException {
        String response = sendRequest("/nonexistent");
        String expected = expectedResponse("DEFAULT", "", "/");
        assertThat(response).isEqualTo(expected);
    }

    @Test
    @RunAsClient
    public void exactMatch() throws IOException, InterruptedException {
        String response = sendRequest("/MappingServlet");
        String expected = expectedResponse("EXACT", "MappingServlet", "/MappingServlet");
        assertThat(response).isEqualTo(expected);
    }

    @Test
    @RunAsClient
    public void extensionMatch() throws IOException, InterruptedException {
        String response = sendRequest("/Some/Text.txt");
        String expected = expectedResponse("EXTENSION", "Some/Text", "*.txt");
        assertThat(response).isEqualTo(expected);
    }

    @Test
    @RunAsClient
    public void pathMatch() throws IOException, InterruptedException {
        String response = sendRequest("/Path/file");
        String expected = expectedResponse("PATH", "file", "/Path/*");
        assertThat(response).isEqualTo(expected);
    }

    private static String sendRequest(String urlPath) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .version(HTTP_2)
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080" + urlPath))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }

    private static String expectedResponse(String mappingMatch, String matchingValue, String pattern) {
        return "Mapping match: " + mappingMatch + "\nServlet name: " + MappingServlet.class.getName() + "\nMatch value: " +
                matchingValue + "\nPattern: " + pattern + "\n";
    }
}
