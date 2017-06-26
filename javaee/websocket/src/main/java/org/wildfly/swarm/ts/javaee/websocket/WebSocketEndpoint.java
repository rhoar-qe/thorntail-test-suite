package org.wildfly.swarm.ts.javaee.websocket;

import javax.annotation.PostConstruct;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint("/echo")
public class WebSocketEndpoint {
    private boolean initialized;

    @PostConstruct
    public void init() {
        this.initialized = true;
    }

    @OnOpen
    public void onOpen(Session session) throws IOException {
        session.getBasicRemote().sendText("initialized = " + initialized);
    }
    
    @OnMessage
    public String onMessage(String message) throws IOException {
        return "ECHO: " + message;
    }
}
