package org.wildfly.swarm.ts.javaee.jaxrs.cdi.jpa.jta;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment
public class JaxrsCdiJpaJtaTest {
    private static String url;

    @Test
    @InSequence(1)
    @RunAsClient
    public void empty() throws IOException {
        String response = Request.Get("http://localhost:8080/").execute().returnContent().asString();
        JsonArray json = JsonParser.parseString(response).getAsJsonArray();
        assertThat(json.size()).isEqualTo(0);
    }

    @Test
    @InSequence(2)
    @RunAsClient
    public void create() throws IOException {
        JsonObject book = new JsonObject();
        book.add("title", new JsonPrimitive("Title"));
        book.add("author", new JsonPrimitive("Author"));
        HttpResponse response = Request.Post("http://localhost:8080/")
                .bodyString(book.toString(), ContentType.APPLICATION_JSON).execute().returnResponse();
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(201);
        url = response.getFirstHeader("Location").getValue();
    }

    @Test
    @InSequence(3)
    @RunAsClient
    public void notEmpty() throws IOException {
        String response = Request.Get("http://localhost:8080/").execute().returnContent().asString();
        JsonArray json = JsonParser.parseString(response).getAsJsonArray();
        assertThat(json.size()).isEqualTo(1);
    }

    @Test
    @InSequence(4)
    @RunAsClient
    public void content() throws IOException {
        String response = Request.Get(url).execute().returnContent().asString();
        JsonObject json = JsonParser.parseString(response).getAsJsonObject();
        assertThat(json.get("title").getAsString()).isEqualTo("Title");
        assertThat(json.get("author").getAsString()).isEqualTo("Author");
    }

    @Test
    @InSequence(5)
    @RunAsClient
    public void delete() throws IOException {
        HttpResponse response = Request.Delete(url).execute().returnResponse();
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(204);
    }

    @Test
    @InSequence(6)
    @RunAsClient
    public void emptyAgain() throws IOException {
        String response = Request.Get("http://localhost:8080/").execute().returnContent().asString();
        JsonArray json = JsonParser.parseString(response).getAsJsonArray();
        assertThat(json.size()).isEqualTo(0);
    }
}
