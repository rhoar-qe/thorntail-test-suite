package org.wildfly.swarm.ts.javaee.jaxrs.cdi;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HelloService {
    public String hello() {
        return "Hello, World!";
    }
}
