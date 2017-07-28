package org.wildfly.swarm.ts.javaee.servlet.cdi;

import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

@SessionScoped
public class GreeterService implements Serializable {
    private AtomicInteger counter = new AtomicInteger();

    public String hello(String name) {
        return "Hello, " + name + "! Counter: " + counter.incrementAndGet();
    }
}
