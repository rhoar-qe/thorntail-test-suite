package org.wildfly.swarm.ts.javaee.interceptors.cdi;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.interceptor.Interceptors;

@ApplicationScoped
@Interceptors(LoggingInterceptor.class)
@Timed
public class HelloService {
    @PostConstruct
    public void init() {
        System.out.println("HelloService initialized");
    }

    public String hello() {
        return "Hello, World!";
    }
}
