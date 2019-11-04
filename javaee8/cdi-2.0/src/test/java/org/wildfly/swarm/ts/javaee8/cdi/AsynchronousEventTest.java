package org.wildfly.swarm.ts.javaee8.cdi;

import java.util.concurrent.ExecutionException;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;
import org.wildfly.swarm.ts.javaee8.cdi.asyncevents.AsyncEventObserver;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment
public class AsynchronousEventTest {
    @Inject
    private AsyncEventObserver asyncEventObserver;

    @Inject
    private Event<String> simpleMessageEvent;

    @Test
    public void testAsynchronousEvents() throws ExecutionException, InterruptedException {
        assertThat(asyncEventObserver.getMessage()).contains("No message arrived yet");
        simpleMessageEvent.fireAsync("Hello").toCompletableFuture().get();
        assertThat(asyncEventObserver.getMessage()).isEqualTo("Hello");
    }

}
