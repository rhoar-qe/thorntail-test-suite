package org.wildfly.swarm.ts.hollow.jar.full;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ProcessResult {
    private List<Object> writtenItems = new ArrayList<>();

    public List<Object> getWrittenItems() {
        return writtenItems;
    }

    public void addWrittenItems(Object writtenItem) {
        this.writtenItems.add(writtenItem);
    }

    public void clear() {
        this.writtenItems.clear();
    }
}
