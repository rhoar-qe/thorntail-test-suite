package org.wildfly.swarm.ts.javaee8.jsf.websocket;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.push.Push;
import javax.faces.push.PushContext;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@ApplicationScoped
public class WebSocketBean {
    @Inject
    @Push(channel = "myChannel")
    private PushContext myChannel;

    private String messageText;

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public void sendMessage() {
        myChannel.send(messageText);
        messageText = null;
    }
}
