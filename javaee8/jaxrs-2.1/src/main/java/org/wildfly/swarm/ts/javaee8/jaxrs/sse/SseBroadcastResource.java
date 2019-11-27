package org.wildfly.swarm.ts.javaee8.jaxrs.sse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;

@Path("/broadcast")
public class SseBroadcastResource {
    private Sse sse;

    public SseBroadcastResource(@Context Sse sse) {
        this.sse = sse;
    }

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void broadcast(@Context SseEventSink eventSink) {
        SseBroadcaster broadcaster = sse.newBroadcaster();
        broadcaster.register(eventSink);

        OutboundSseEvent event = sse.newEventBuilder()
                .name("Hello World")
                .mediaType(MediaType.TEXT_PLAIN_TYPE)
                .data(String.class, "Broadcast message")
                .build();

        broadcaster.broadcast(event)
                .whenComplete((r, ex) -> {
                    broadcaster.close();
                });
    }
}
