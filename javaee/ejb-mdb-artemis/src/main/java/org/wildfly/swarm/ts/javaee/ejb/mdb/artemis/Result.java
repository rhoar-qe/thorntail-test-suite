package org.wildfly.swarm.ts.javaee.ejb.mdb.artemis;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class Result {
    private final List<String> items = new ArrayList<>();

    public synchronized List<String> getItems() {
        return new ArrayList<>(items);
    }

    public synchronized void addItem(String item) {
        this.items.add(item);
    }
}
