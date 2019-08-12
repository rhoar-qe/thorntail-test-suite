package org.wildfly.swarm.ts.javaee.ejb.mdb.artemis;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
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
public class EjbMdbArtemisTest {
    @ClassRule
    public static Docker amq7 = DockerContainers.amq7();

    @Test
    @RunAsClient
    public void sendAndReceiveMessages() throws IOException {
        StringBuilder expected = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            expected.append("msg ").append(i);

            HttpResponse sendResponse = Request.Post("http://localhost:8080/")
                    .bodyString("msg " + i, ContentType.TEXT_PLAIN).execute().returnResponse();
            assertThat(sendResponse.getStatusLine().getStatusCode()).isEqualTo(200);

            await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
                String receiveResponse = Request.Get("http://localhost:8080/").execute().returnContent().asString();
                assertThat(receiveResponse).isEqualTo(expected.toString());
            });

            expected.append("|");
        }
    }
}
