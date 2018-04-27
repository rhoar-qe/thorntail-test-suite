package org.wildfly.swarm.ts.javaee.messaging.remote;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@MessageDriven(name = "QueueReader", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:/jms/queue/my-queue"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
})
public class QueueReader implements MessageListener {
    static final Set<String> acceptedMessages = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Override
    public void onMessage(Message message) {
        try {
            String text = ((TextMessage) message).getText();
            acceptedMessages.add(text);
        } catch (JMSException e) {
        }
    }
}
