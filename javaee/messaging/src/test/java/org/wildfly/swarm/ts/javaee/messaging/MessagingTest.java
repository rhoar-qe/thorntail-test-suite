package org.wildfly.swarm.ts.javaee.messaging;

import org.apache.http.client.fluent.Request;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@RunWith(Arquillian.class)
@DefaultDeployment
public class MessagingTest {
    @Test
    @RunAsClient
    public void testMessaging() throws IOException {
        String response = Request.Get("http://localhost:8080/messaging?operation=sendQueue").execute().returnContent().asString();
        assertThat(response).isEqualTo("OK");

        String resultsUrl = "http://localhost:8080/messaging?operation=results";

        await().atMost(1, TimeUnit.SECONDS).untilAsserted(() -> {
            assertThat(Request.Get(resultsUrl).execute().returnContent().asString()).isEqualTo("1 in queue\n");
        });

        response = Request.Get("http://localhost:8080/messaging?operation=sendTopic").execute().returnContent().asString();
        assertThat(response).isEqualTo("OK");

        await().atMost(1, TimeUnit.SECONDS).untilAsserted(() -> {
            assertThat(Request.Get(resultsUrl).execute().returnContent().asString()).isEqualTo("2 in topic\n");
        });
    }
}
