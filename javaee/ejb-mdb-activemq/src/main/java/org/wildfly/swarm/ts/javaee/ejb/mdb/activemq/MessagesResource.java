package org.wildfly.swarm.ts.javaee.ejb.mdb.activemq;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class MessagesResource {
    @Inject
    private Result result;

    // the ActiveMQ resource adapter doesn't support JMS 2.0 client APIs

    @Resource(lookup = "java:comp/DefaultJMSConnectionFactory")
    private ConnectionFactory connectionFactory;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String receivedMessages() {
        return String.join("|", result.getItems());
    }

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public Response sendMessage(String text) throws JMSException {
        Connection connection = connectionFactory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue("my-queue");
        session.createProducer(queue).send(queue, session.createTextMessage(text));
        session.close();
        connection.close();
        return Response.ok().build();
    }
}
