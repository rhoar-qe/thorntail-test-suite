package org.wildfly.swarm.ts.javaee.websocket;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.undertow.WARArchive;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class WebSocketTest {
    // can't use `@DefaultDeployment` because content of `src/main/webapp` is never copied
    // to `target/[test-]classes`, and that's what @DefaultDeployment puts into the archive
    @Deployment
    public static Archive<?> deployment() {
        return ShrinkWrap.create(WARArchive.class)
                .addPackage(WebSocketTest.class.getPackage())
                .addAsWebResource(new File("src/main/webapp/index.html"))
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/beans.xml"));
    }

    @Test
    @RunAsClient
    public void test() throws IOException, DeploymentException, InterruptedException {
        TestClient client = new TestClient();

        WebSocketContainer wsContainer = ContainerProvider.getWebSocketContainer();
        try (Session session = wsContainer.connectToServer(client, URI.create("ws://localhost:8080/echo"))) {
            client.openLatch.await(1, TimeUnit.SECONDS);
            client.firstReceiveLatch.await(1, TimeUnit.SECONDS);
            assertThat(client.receivedResponse).isEqualTo("initialized = true");
            client.secondReceiveLatch.await(1, TimeUnit.SECONDS);
            assertThat(client.receivedResponse).isEqualTo("ECHO: foobar");
        }
        client.closeLatch.await(1, TimeUnit.SECONDS);
    }

    @ClientEndpoint
    public static class TestClient {
        public final CountDownLatch openLatch = new CountDownLatch(1);
        public final CountDownLatch firstReceiveLatch = new CountDownLatch(1);
        public final CountDownLatch secondReceiveLatch = new CountDownLatch(1);
        public final CountDownLatch closeLatch = new CountDownLatch(1);

        private boolean firstResponseReceived;
        public volatile String receivedResponse;

        @OnOpen
        public void onOpen(Session session) throws IOException {
            openLatch.countDown();
        }

        @OnMessage
        public void onMessage(String message, Session session) throws IOException {
            receivedResponse = message;

            if (!firstResponseReceived) {
                firstResponseReceived = true;
                firstReceiveLatch.countDown();
                session.getBasicRemote().sendText("foobar");
            } else {
                secondReceiveLatch.countDown();
            }
        }

        @OnClose
        public void onClose() {
            closeLatch.countDown();
        }
    }
}
