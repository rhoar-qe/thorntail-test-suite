package org.wildfly.swarm.ts.microprofile.rest.client.v14.clientheaders;

import java.util.concurrent.atomic.AtomicInteger;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Counter {

    private static final AtomicInteger COUNT = new AtomicInteger(0);

    public int count() {
        return COUNT.incrementAndGet();
    }
}
