package org.wildfly.swarm.ts.javaee.ejb.mdb.amqp;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@ApplicationScoped
public class Result {
    private Queue<String> items = new ConcurrentLinkedQueue<>();

    public List<String> getItems() {
        return new ArrayList<>(items);
    }

    public void addItem(String item) {
        this.items.add(item);
    }
}
