package org.wildfly.swarm.ts.javaee8.cdi.interceptionfactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MyGreetingService implements GreetingService {
    private String greeting;

    @Override
    public String getGreeting() {
        return greeting;
    }

    @Override
    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }
}
