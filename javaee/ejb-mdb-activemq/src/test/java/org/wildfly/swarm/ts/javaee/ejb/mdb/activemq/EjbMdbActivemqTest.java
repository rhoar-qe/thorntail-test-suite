package org.wildfly.swarm.ts.javaee.ejb.mdb.activemq;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;
import org.wildfly.swarm.ts.common.docker.Docker;
import org.wildfly.swarm.ts.common.docker.DockerContainers;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@RunWith(Arquillian.class)
@DefaultDeployment
public class EjbMdbActivemqTest {
    @ClassRule
    public static Docker amq7 = DockerContainers.amq7();

    @Test
    @RunAsClient
    @InSequence(1)
    public void sendMessages() throws IOException {
        for (int i = 0; i < 10; i++) {
            HttpResponse response = Request.Post("http://localhost:8080/")
                    .bodyString("msg " + i, ContentType.TEXT_PLAIN).execute().returnResponse();
            assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
        }
    }

    @Test
    @RunAsClient
    @InSequence(2)
    public void receivewMessages() {
        String expected = "msg 0|msg 1|msg 2|msg 3|msg 4|msg 5|msg 6|msg 7|msg 8|msg 9";
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            assertThat(Request.Get("http://localhost:8080/").execute().returnContent().asString()).isEqualTo(expected);
        });
    }
}
