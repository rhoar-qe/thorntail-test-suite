package org.wildfly.swarm.ts.javaee.ejb.passivation;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.PostActivate;
import javax.ejb.PrePassivate;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Stateful
public class HelloBean {
    static final Set<String> lifecycleMessages = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private static final AtomicInteger counter = new AtomicInteger();

    private int id;

    public String hello() {
        return "Hello from stateful EJB " + id;
    }

    @PostConstruct
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void postConstruct() {
        this.id = counter.getAndIncrement();
        lifecycleMessages.add("Constructed HelloBean " + id);
    }

    @PrePassivate
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void prePassivate() {
        lifecycleMessages.add("Passivating HelloBean " + id);
    }

    @PostActivate
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void postActivate() {
        lifecycleMessages.add("Activated HelloBean " + id);
    }

    @PreDestroy
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void preDestroy() {
        lifecycleMessages.add("Destroying HelloBean " + id);
    }
}
