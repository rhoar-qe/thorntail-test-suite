package org.wildfly.swarm.ts.javaee.ejb.passivation;

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
public class EjbPassivationTest {
    @Test
    @RunAsClient
    public void test() throws IOException {
        for (int i = 0; i < 100; i++) {
            String response = Request.Get("http://localhost:8080/").execute().returnContent().asString();
            assertThat(response).isEqualTo("Hello from stateful EJB " + i);
        }

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            String response = Request.Get("http://localhost:8080/lifecycle-messages").execute().returnContent().asString();

            for (int i = 0; i < 100; i++) {
                assertThat(response).contains("Constructed HelloBean " + i);
            }

            // can't determine which exact beans will stay alive or precisely how many beans will be passivated,
            // but 90 should be a good lower bound
            int passivated = 0;
            for (int i = 0; i < 100; i++) {
                if (response.contains("Passivating HelloBean " + i)) {
                    passivated++;
                }
            }
            assertThat(passivated).isGreaterThanOrEqualTo(90);
        });
    }
}
