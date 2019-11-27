package org.wildfly.swarm.ts.javaee8.jaxrs.rclient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;

@Path("/")
public class HelloResource {
    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void get(@Context SseEventSink eventSink, @Context Sse sse) {
        try (SseEventSink sink = eventSink) {
            OutboundSseEvent.Builder builder = sse.newEventBuilder();
            sink.send(builder.data("Hello from JAX-RS 2.1").build());
        }
    }
}
