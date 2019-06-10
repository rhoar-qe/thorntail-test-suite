package org.wildfly.swarm.ts.microprofile.metrics.v20;

import org.eclipse.microprofile.metrics.annotation.Counted;

import javax.enterprise.context.ApplicationScoped;
import java.util.Random;

@ApplicationScoped
public class AnotherHelloService {
    @Counted(
            name = "hello-count",
            absolute = true,
            displayName = "Hello Count",
            description = "Number of hello invocations",
            reusable = true
    )
    public String hello() throws InterruptedException {
        Thread.sleep(new Random().nextInt(100) + 1);
        return "Hello from another counted method";
    }
}
