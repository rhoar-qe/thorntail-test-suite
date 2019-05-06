package org.wildfly.swarm.ts.microprofile.fault.tolerance.v20.priority;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class InterceptorsContext {
    private final Queue<String> orderQueue = new ConcurrentLinkedDeque<>();

    public Queue<String> getOrderQueue() {
        return orderQueue;
    }
}