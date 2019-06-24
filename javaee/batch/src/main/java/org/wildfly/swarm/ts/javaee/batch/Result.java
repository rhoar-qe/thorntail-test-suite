package org.wildfly.swarm.ts.javaee.batch;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@ApplicationScoped
public class Result {
    private final List<Object> items = new ArrayList<>();

    public synchronized List<Object> getItems() {
        return new ArrayList<>(items);
    }

    public synchronized void addItem(Object item) {
        this.items.add(item);
    }

    public synchronized void clear() {
        this.items.clear();
    }
}
