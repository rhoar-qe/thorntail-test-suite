package org.wildfly.swarm.ts.javaee.jmx;

import org.apache.http.client.fluent.Request;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.lang.management.ManagementFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Need to use JUnit's assertion methods instead of AssertJ because AssertJ isn't available in the in-containe
 */
@RunWith(Arquillian.class)
@DefaultDeployment
public class JmxTest {
    @Test
    @RunAsClient
    public void hello() throws IOException {
        String response = Request.Get("http://localhost:8080/").execute().returnContent().asString();
        assertEquals("Hello", response);
    }

    @Test
    public void localJmx() throws JMException {
        MBeanServer jmx = ManagementFactory.getPlatformMBeanServer();

        String os = (String) jmx.getAttribute(new ObjectName("java.lang:type=OperatingSystem"), "Name");
        assertEquals(System.getProperty("os.name"), os);

        // the jboss.as domains aren't available from inside the deployment, which is probably intentional?
    }

    @Test
    @RunAsClient
    public void remoteJmx() throws IOException, JMException {
        JMXServiceURL url = new JMXServiceURL("service:jmx:remote+http://localhost:9990");
        try (JMXConnector connector = JMXConnectorFactory.connect(url)) {
            MBeanServerConnection jmx = connector.getMBeanServerConnection();

            String os = (String) jmx.getAttribute(new ObjectName("java.lang:type=OperatingSystem"), "Name");
            assertEquals(System.getProperty("os.name"), os);

            Boolean showModel = (Boolean) jmx.getAttribute(new ObjectName("jboss.as:subsystem=jmx"), "showModel");
            assertTrue(showModel);
        }
    }
}
