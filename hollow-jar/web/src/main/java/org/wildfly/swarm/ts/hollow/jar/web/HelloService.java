package org.wildfly.swarm.ts.hollow.jar.web;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HelloService {
    public String hello(String name) {
        return "Hello, " + name + "!";
    }
}
