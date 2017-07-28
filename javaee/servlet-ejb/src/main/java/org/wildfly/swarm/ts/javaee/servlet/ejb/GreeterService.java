package org.wildfly.swarm.ts.javaee.servlet.ejb;

import javax.ejb.Stateful;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

@Stateful
public class GreeterService implements Serializable {
    private AtomicInteger counter = new AtomicInteger();

    public String hello(String name) {
        return "Hello, " + name + "! Counter: " + counter.incrementAndGet();
    }
}
