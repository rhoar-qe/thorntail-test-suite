package org.wildfly.swarm.ts.javaee8.websocket;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.annotation.PostConstruct;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/echo")
public class WebSocketEndpoint {
    private boolean initialized;

    @PostConstruct
    public void init() {
        this.initialized = true;
    }

    @OnOpen
    public void onOpen(Session session) throws IOException {
        session.addMessageHandler(String.class, text -> {
            try {
                session.getBasicRemote().sendText("ECHO: " + text);
            } catch (IOException e) {
            }
        });
        session.addMessageHandler(ByteBuffer.class, bytes -> {
            try {
                session.getBasicRemote().sendBinary(bytes);
            } catch (IOException e) {
            }
        });

        session.getBasicRemote().sendText("initialized = " + initialized);
    }
}
