package org.wildfly.swarm.ts.javaee.naming;

import javax.ejb.Stateless;

@Stateless
public class HelloBean {
    public String hello() {
        return "Hello from EJB looked up via name";
    }
}
