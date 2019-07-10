package org.wildfly.swarm.ts.javaee8.websocket;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.undertow.WARArchive;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class WebSocketTest {
    // can't use `@DefaultDeployment` because content of `src/main/webapp` is never copied
    // to `target/[test-]classes`, and that's what @DefaultDeployment puts into the archive
    @Deployment
    public static Archive<?> deployment() {
        return ShrinkWrap.create(WARArchive.class)
                .addPackage(WebSocketTest.class.getPackage())
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
            assertThat(client.receivedText).isEqualTo("initialized = true");
            client.secondReceiveLatch.await(1, TimeUnit.SECONDS);
            assertThat(client.receivedText).isEqualTo("ECHO: foobar");
            client.thirdReceiveLatch.await(1, TimeUnit.SECONDS);
            assertThat(client.receivedBytes).isEqualTo(new byte[]{1, 2, 3});
        }
        client.closeLatch.await(1, TimeUnit.SECONDS);
    }

    @ClientEndpoint
    public static class TestClient {
        public final CountDownLatch openLatch = new CountDownLatch(1);

        public final CountDownLatch firstReceiveLatch = new CountDownLatch(1);

        public final CountDownLatch secondReceiveLatch = new CountDownLatch(1);

        public final CountDownLatch thirdReceiveLatch = new CountDownLatch(1);

        public final CountDownLatch closeLatch = new CountDownLatch(1);

        private boolean firstResponseReceived;

        public volatile String receivedText;

        public volatile byte[] receivedBytes;

        @OnOpen
        public void onOpen(Session session) {
            openLatch.countDown();
            session.addMessageHandler(String.class, text -> {
                receivedText = text;

                if (!firstResponseReceived) {
                    firstResponseReceived = true;
                    firstReceiveLatch.countDown();
                    try {
                        session.getBasicRemote().sendText("foobar");
                    } catch (IOException e) {
                    }
                } else {
                    secondReceiveLatch.countDown();
                    byte[] bytes = {1, 2, 3};
                    try {
                        session.getBasicRemote().sendBinary(ByteBuffer.wrap(bytes));
                    } catch (IOException e) {
                    }
                }
            });
            session.addMessageHandler(ByteBuffer.class, bytes -> {
                receivedBytes = bytes.array();
            });
        }

        @OnClose
        public void onClose() {
            closeLatch.countDown();
        }
    }
}
