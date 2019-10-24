package org.wildfly.swarm.ts.javaee8.cdi.observerordering;

import java.util.List;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Named;

@Named
@ApplicationScoped
public class Observer {
    public void observeEvent2(@Observes @Priority(2) List<Integer> ordering) {
        ordering.add(2);
    }

    public void observeEvent3(@Observes @Priority(3) List<Integer> ordering) {
        ordering.add(3);
    }

    public void observeEvent1(@Observes @Priority(1) List<Integer> ordering) {
        ordering.add(1);
    }
}
