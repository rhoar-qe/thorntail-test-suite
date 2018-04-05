package org.wildfly.swarm.ts.javaee.messaging;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@MessageDriven(name = "TopicMessagingMDB", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:/jms/topic/my-topic"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
})
public class TopicMessagingMdb implements MessageListener {
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
