package org.wildfly.swarm.ts.javaee.batch;

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
public class BatchTest {
    @Test
    @RunAsClient
    public void test() throws IOException {
        {
            String response = Request.Get("http://localhost:8080/batch?operation=start").execute().returnContent().asString();
            assertThat(response).isEqualTo("1");
        }

        await().atMost(1, TimeUnit.SECONDS).untilAsserted(() -> {
            String response = Request.Get("http://localhost:8080/batch?operation=results").execute().returnContent().asString();
            assertThat(response).isEqualTo("0\n1\n2\n3\n4\n5\n6\n7\n8\n9\n");
        });
    }
}
