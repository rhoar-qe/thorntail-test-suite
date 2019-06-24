package org.wildfly.swarm.ts.javaee.messaging;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@ApplicationScoped
public class Result {
    private List<String> items = new ArrayList<>();

    public synchronized List<String> getItems() {
        return new ArrayList<>(items);
    }

    public synchronized void addItem(String item) {
        this.items.add(item);
    }

    public synchronized void clear() {
        this.items.clear();
    }
}
