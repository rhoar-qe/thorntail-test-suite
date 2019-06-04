package org.wildfly.swarm.ts.javaee.ejb.mdb.amqp;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "connectionFactory", propertyValue = "my-factory"),
        @ActivationConfigProperty(propertyName = "user", propertyValue = "amq"),
        @ActivationConfigProperty(propertyName = "password", propertyValue = "amq"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "my-queue"),
        @ActivationConfigProperty(propertyName = "jndiParameters", propertyValue = "java.naming.factory.initial=org.apache.qpid.jms.jndi.JmsInitialContextFactory;connectionFactory.my-factory=amqp://localhost:5672;queue.my-queue=my-queue"),
})
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
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
