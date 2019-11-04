package org.wildfly.swarm.ts.javaee8.cdi;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment
public class ObserverEventTest {
    private List<Integer> ordering = new ArrayList<>();

    @Inject
    private Event<List<Integer>> event;

    @Test
    public void testObserverOrdering() {
        assertThat(ordering).isEmpty();
        event.fire(ordering);
        assertThat(ordering).containsExactly(1, 2, 3);
    }

}
