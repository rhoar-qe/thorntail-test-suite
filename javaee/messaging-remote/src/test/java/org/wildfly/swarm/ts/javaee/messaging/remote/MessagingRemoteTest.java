package org.wildfly.swarm.ts.javaee.messaging.remote;

import org.apache.http.client.fluent.Request;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@RunWith(Arquillian.class)
@DefaultDeployment
public class MessagingRemoteTest {
    @Test
    @RunAsClient
    public void test() throws NamingException {
        Hashtable<String, Object> jndiProps = new Hashtable<>();
        jndiProps.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        jndiProps.put(Context.PROVIDER_URL, "remote+http://localhost:8080");

        Context ctx = null;
        try {
            ctx = new InitialContext(jndiProps);
            ConnectionFactory connectionFactory = (ConnectionFactory) ctx.lookup("jms/RemoteConnectionFactory");
            Queue queue = (Queue) ctx.lookup("jms/queue/my-queue");

            try (JMSContext jms = connectionFactory.createContext("test-user", "test-user-password")) {
                JMSProducer producer = jms.createProducer();
                producer.send(queue, "Hello 1");
                producer.send(queue, "Hello 2");
            }

            await().atMost(1, TimeUnit.SECONDS).untilAsserted(() -> {
                String response = Request.Get("http://localhost:8080/").execute().returnContent().asString();
                assertThat(response).contains("Hello 1");
                assertThat(response).contains("Hello 2");
            });

            ctx.close();
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }
}
