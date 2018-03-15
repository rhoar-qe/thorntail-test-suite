package org.wildfly.swarm.ts.microprofile.v10;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HelloService {
    public String hello() {
        return "Hello, World!";
    }
}
