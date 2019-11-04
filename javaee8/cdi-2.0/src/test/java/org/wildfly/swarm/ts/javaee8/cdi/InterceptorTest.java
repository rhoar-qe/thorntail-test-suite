package org.wildfly.swarm.ts.javaee8.cdi;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;
import org.wildfly.swarm.ts.javaee8.cdi.interceptionfactory.GreetingService;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment
public class InterceptorTest {
    @Inject
    private GreetingService service;

    @Test
    public void test() {
        assertThat(service.getGreeting()).isNull();
        service.setGreeting("Hello");
        assertThat(service.getGreeting()).isEqualTo("Param: Hello");
    }
}
