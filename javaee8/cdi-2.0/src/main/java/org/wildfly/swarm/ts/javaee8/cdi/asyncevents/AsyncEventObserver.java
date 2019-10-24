package org.wildfly.swarm.ts.javaee8.cdi.asyncevents;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Named;

@Named
@ApplicationScoped
public class AsyncEventObserver {
    private String message = "No message arrived yet";

    public String getMessage() {
        return message;
    }

    public void observeEvent(@ObservesAsync String message) {
        this.message = message;
    }

}
