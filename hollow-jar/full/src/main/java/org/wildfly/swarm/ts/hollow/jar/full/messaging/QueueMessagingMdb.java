package org.wildfly.swarm.ts.hollow.jar.full.messaging;

import org.wildfly.swarm.ts.hollow.jar.full.ProcessResult;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@MessageDriven(name = "QueueMessagingMDB", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:/jms/queue/my-queue"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
})
public class QueueMessagingMdb implements MessageListener {
    @Inject
    private ProcessResult processResult;

    @Override
    public void onMessage(Message message) {
        try {
            String text = ((TextMessage) message).getText();
            processResult.addWrittenItems(text);
        } catch (JMSException e) {
        }
    }
}
