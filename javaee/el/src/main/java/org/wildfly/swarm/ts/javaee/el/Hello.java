package org.wildfly.swarm.ts.javaee.el;

public class Hello {
    private final String name;

    public Hello(String name) {
        this.name = name;
    }

    public String getGreeting() {
        return "Hello, " + name + "!";
    }
}
