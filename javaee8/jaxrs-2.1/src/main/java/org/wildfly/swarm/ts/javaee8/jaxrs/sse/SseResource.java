package org.wildfly.swarm.ts.javaee8.jaxrs.sse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;

@Path("/simple")
public class SseResource {
    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void getServerSentEvents(@Context SseEventSink eventSink, @Context Sse sse) {
        try (SseEventSink sink = eventSink) {
            OutboundSseEvent event = sse.newEventBuilder()
                    .name("message-to-client")
                    .data(String.class, "Hello world!")
                    .build();
            sink.send(event);
        }
    }
}
