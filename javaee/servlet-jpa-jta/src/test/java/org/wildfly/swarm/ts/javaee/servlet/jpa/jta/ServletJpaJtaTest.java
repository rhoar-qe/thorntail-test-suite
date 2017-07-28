package org.wildfly.swarm.ts.javaee.servlet.jpa.jta;

import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicNameValuePair;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import javax.annotation.security.RunAs;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment
public class ServletJpaJtaTest {
    private static int id = -1;

    @Test
    @RunAsClient
    @InSequence(1)
    public void empty() throws IOException {
        String result = Request.Get("http://localhost:8080/").execute().returnContent().asString();
        assertThat(result).isEmpty();
    }

    @Test
    @RunAsClient
    @InSequence(2)
    public void create() throws IOException {
        String result = Request.Post("http://localhost:8080/").bodyForm(
                new BasicNameValuePair("title", "Title"),
                new BasicNameValuePair("author", "Author")
        ).execute().returnContent().asString();

        assertThat(result).isNotEmpty();
        id = Integer.parseInt(result);
    }

    @Test
    @RunAsClient
    @InSequence(3)
    public void notEmpty() throws IOException {
        String result = Request.Get("http://localhost:8080/").execute().returnContent().asString();
        assertThat(result).isNotEmpty().isEqualTo("1 Author: Title\n");
    }

    @Test
    @RunAsClient
    @InSequence(4)
    public void delete() throws IOException {
        String result = Request.Delete("http://localhost:8080/?id=" + id).execute().returnContent().asString();
        assertThat(result).isEqualTo(id + " deleted");
    }

    @Test
    @RunAsClient
    @InSequence(5)
    public void emptyAgain() throws IOException {
        String result = Request.Get("http://localhost:8080/").execute().returnContent().asString();
        assertThat(result).isEmpty();
    }
}
