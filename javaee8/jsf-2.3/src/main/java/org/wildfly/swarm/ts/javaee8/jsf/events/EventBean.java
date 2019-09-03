package org.wildfly.swarm.ts.javaee8.jsf.events;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.event.PostRenderViewEvent;
import javax.inject.Named;

@Named
@ApplicationScoped
public class EventBean {
    private String message = "before";

    public String getMessage() {
        return message;
    }

    public void handleEvent(PostRenderViewEvent event) {
        message = "after";
    }
}
