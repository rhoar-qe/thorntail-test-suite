package org.wildfly.swarm.ts.wildfly.request.controller;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import javax.json.Json;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment
public class SuspendResumeTest {
    @Test
    @RunAsClient
    @InSequence(1)
    public void helloIsOk() throws IOException {
        String response = Request.Get("http://localhost:8080/").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello");
    }

    @Test
    @RunAsClient
    @InSequence(2)
    public void suspend() throws IOException {
        String json = Json.createObjectBuilder()
                .add("operation", "suspend")
                .build()
                .toString();
        HttpResponse response = Request.Post("http://localhost:9990/management")
                .bodyString(json, ContentType.APPLICATION_JSON)
                .execute()
                .returnResponse();
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
    }


    @Test
    @RunAsClient
    @InSequence(3)
    public void helloFailsWhenSuspended() throws IOException {
        HttpResponse response = Request.Get("http://localhost:8080/").execute().returnResponse();
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(503);
    }

    @Test
    @RunAsClient
    @InSequence(4)
    public void resume() throws IOException {
        String json = Json.createObjectBuilder()
                .add("operation", "resume")
                .build()
                .toString();
        HttpResponse response = Request.Post("http://localhost:9990/management")
                .bodyString(json, ContentType.APPLICATION_JSON)
                .execute()
                .returnResponse();
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
    }

    @Test
    @RunAsClient
    @InSequence(5)
    public void helloIsOkAgainAfterResume() throws IOException {
        String response = Request.Get("http://localhost:8080/").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello");
    }
}
