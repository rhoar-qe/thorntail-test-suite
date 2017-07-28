package org.wildfly.swarm.ts.javaee.cdi.ejb;

import javax.ejb.Singleton;

@Singleton
public class GreeterBean {
    public String hello(String name) {
        return "Hello, " + name + "!";
    }
}
