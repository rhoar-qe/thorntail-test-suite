package org.wildfly.swarm.ts.javaee.ejb.mdb.artemis;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "my-queue"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "useJNDI", propertyValue = "false"),
})
public class QueueMdb implements MessageListener {
    @Inject
    private Result result;

    @Override
    public void onMessage(Message message) {
        try {
            String text = ((TextMessage) message).getText();
            result.addItem(text);
        } catch (JMSException e) {
        }
    }
}
