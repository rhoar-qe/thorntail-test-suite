package org.wildfly.swarm.ts.hollow.jar.full.ejb.passivation;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.PostActivate;
import javax.ejb.PrePassivate;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.wildfly.swarm.ts.hollow.jar.full.ProcessResult;

import java.util.concurrent.atomic.AtomicInteger;

@Stateful
public class EjbPassivationBean {
    private static final AtomicInteger counter = new AtomicInteger();

    @Inject
    private ProcessResult processResult;

    private int id;

    public String hello() {
        return "Hello from stateful EJB " + id;
    }

    @PostConstruct
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void postConstruct() {
        this.id = counter.getAndIncrement();
        processResult.addWrittenItems("Constructed EjbPassivationBean " + id);
    }

    @PrePassivate
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void prePassivate() {
        processResult.addWrittenItems("Passivating EjbPassivationBean " + id);
    }

    @PostActivate
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void postActivate() {
        processResult.addWrittenItems("Activated EjbPassivationBean " + id);
    }

    @PreDestroy
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void preDestroy() {
        processResult.addWrittenItems("Destroying EjbPassivationBean " + id);
    }
}
