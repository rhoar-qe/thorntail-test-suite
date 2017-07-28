package org.wildfly.swarm.ts.javaee.interceptors.ejb;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

@Stateless
@Interceptors(LoggingInterceptor.class)
public class HelloService {
    @PostConstruct
    public void init() {
        System.out.println("HelloService initialized");
    }

    public String hello() {
        return "Hello, World!";
    }
}
