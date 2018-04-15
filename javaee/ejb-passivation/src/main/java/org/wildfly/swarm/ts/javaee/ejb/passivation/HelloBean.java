package org.wildfly.swarm.ts.javaee.ejb.passivation;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.PostActivate;
import javax.ejb.PrePassivate;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

@Stateful
public class HelloBean {
    private static final Logger log = Logger.getLogger(HelloBean.class.getSimpleName());

    private static final AtomicInteger counter = new AtomicInteger();

    private int id;

    public String hello() {
        return "Hello from stateful EJB " + id;
    }

    @PostConstruct
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void postConstruct() {
        this.id = counter.getAndIncrement();
        log.info("Constructed HelloBean " + id);
    }

    @PrePassivate
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void prePassivate() {
        log.info("Passivating HelloBean " + id);
    }

    @PostActivate
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void postActivate() {
        log.info("Activated HelloBean " + id);
    }

    @PreDestroy
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void preDestroy() {
        log.info("Destroying HelloBean " + id);
    }
}
