package org.wildfly.swarm.ts.cdi.ejb;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;

/**
 * Can't use AssertJ because this is an in-container test with a JAR deployment. That means dependencies aren't added
 * to the deployment neither by Swarm nor by Arquillian.
 */
@RunWith(Arquillian.class)
@DefaultDeployment(type = DefaultDeployment.Type.JAR)
public class CdiEjbTest {
    @Inject
    private GreeterFacade greeter;

    @Test
    public void test() {
        assertEquals("Said: Hello, Ladicek!", greeter.hello("Ladicek"));
    }
}
