package org.wildfly.swarm.ts.javaee.concurrency;

import java.util.concurrent.Callable;

public class ComputeHello implements Callable<String> {
    @Override
    public String call() throws Exception {
        // heavy-duty computation
        return "Hello, World!";
    }
}
