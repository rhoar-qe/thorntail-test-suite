package org.wildfly.swarm.ts.javaee.cdi;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Greeter {
    public String hello() {
        return "Hello from servlet with CDI";
    }
}
