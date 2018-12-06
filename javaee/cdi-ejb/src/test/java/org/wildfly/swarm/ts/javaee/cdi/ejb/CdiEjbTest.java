package org.wildfly.swarm.ts.javaee.cdi.ejb;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment(type = DefaultDeployment.Type.JAR)
public class CdiEjbTest {
    @Inject
    private GreeterFacade greeter;

    @Test
    public void test() {
        assertThat(greeter.hello("Ladicek")).isEqualTo("Said: Hello, Ladicek!");
    }
}
