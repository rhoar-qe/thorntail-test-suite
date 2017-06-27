package org.wildfly.swarm.ts.fraction.autodetection.cdi.jaxrs.ejb;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HelloService {
    public String hello(String name) {
        return "Hello, " + name + "!";
    }
}
