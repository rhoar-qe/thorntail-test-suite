package org.wildfly.swarm.ts.javaee8.jpa;

import org.apache.http.client.fluent.Request;
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
public class JpaTest {
    @Test
    @RunAsClient
    @InSequence(1)
    public void createFirst() throws IOException {
        String response = Request.Post("http://localhost:8080/?year=1997&month=8&day=24&hour=12&minute=00").execute().returnContent().asString();
        assertThat(response).isEqualTo("1: 1997-08-24 12:00, f:l\n");
    }

    @Test
    @RunAsClient
    @InSequence(2)
    public void createSecond() throws IOException {
        String response = Request.Post("http://localhost:8080/?year=2001&month=9&day=11&hour=9&minute=3").execute().returnContent().asString();
        assertThat(response).isEqualTo("1: 1997-08-24 12:00, f:l\n2: 2001-09-11 09:03, f:l\n");
    }

    @Test
    @RunAsClient
    @InSequence(3)
    public void getIds() throws IOException {
        String response = Request.Get("http://localhost:8080/").execute().returnContent().asString();
        assertThat(response).isEqualTo("1, 2");
    }
}
