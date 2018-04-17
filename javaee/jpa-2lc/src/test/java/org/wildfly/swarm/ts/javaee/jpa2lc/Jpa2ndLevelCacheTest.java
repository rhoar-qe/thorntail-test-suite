package org.wildfly.swarm.ts.javaee.jpa2lc;

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
public class Jpa2ndLevelCacheTest {
    private static String id1;
    private static String id2;

    @Test
    @InSequence(1)
    @RunAsClient
    public void createGreetings() throws IOException {
        id1 = Request.Post("http://localhost:8080/?text=Hello1").execute().returnContent().asString();
        id2 = Request.Post("http://localhost:8080/?text=Hello2").execute().returnContent().asString();

        String greetings = Request.Get("http://localhost:8080/").execute().returnContent().asString();
        assertThat(greetings).isEqualTo("Hello1\nHello2\n");
    }

    @Test
    @InSequence(2)
    @RunAsClient
    public void subvertOneGreeting() throws IOException {
        String affectedRows = Request.Post("http://localhost:8080/" + id1 + "?text=UpdatedHello1").execute().returnContent().asString();
        assertThat(affectedRows).isEqualTo("1");
    }

    @Test
    @InSequence(3)
    @RunAsClient
    public void differenceBetweenCacheAndDatabase() throws IOException {
        String jdbc = Request.Get("http://localhost:8080/" + id1 + "?jdbc=true").execute().returnContent().asString();
        String jpa = Request.Get("http://localhost:8080/" + id1).execute().returnContent().asString();

        assertThat(jdbc).isEqualTo("UpdatedHello1");
        assertThat(jpa).isEqualTo("Hello1");
    }

    @Test
    @InSequence(4)
    @RunAsClient
    public void isGreetingCachedBeforeAndAfterDelete() throws IOException {
        String cachedBeforeDelete = Request.Get("http://localhost:8080/" + id2 + "/cached").execute().returnContent().asString();
        Request.Delete("http://localhost:8080/" + id2).execute().discardContent();
        String cachedAfterDelete = Request.Get("http://localhost:8080/" + id2 + "/cached").execute().returnContent().asString();

        assertThat(cachedBeforeDelete).isEqualTo("true");
        assertThat(cachedAfterDelete).isEqualTo("false");
    }
}
