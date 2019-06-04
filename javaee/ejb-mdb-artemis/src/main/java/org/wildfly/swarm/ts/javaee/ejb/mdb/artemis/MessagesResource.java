package org.wildfly.swarm.ts.javaee.ejb.mdb.artemis;

import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.Queue;
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

    @Inject
    private JMSContext jms;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String receivedMessages() {
        return String.join("|", result.getItems());
    }

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public Response sendMessage(String text) {
        Queue queue = jms.createQueue("my-queue");
        jms.createProducer().send(queue, text);
        return Response.ok().build();
    }
}
