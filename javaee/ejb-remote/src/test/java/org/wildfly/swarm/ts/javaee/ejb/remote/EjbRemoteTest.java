package org.wildfly.swarm.ts.javaee.ejb.remote;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.util.Hashtable;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment
public class EjbRemoteTest {
    @Test
    @RunAsClient
    public void test() throws NamingException {
        Hashtable<String, Object> jndiProps = new Hashtable<>();
        jndiProps.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");

        Context ctx = null;
        try {
            ctx = new InitialContext(jndiProps);
            RemoteEjb service = (RemoteEjb) ctx.lookup("ejb:/EjbRemoteTest/RemoteEjbImpl!org.wildfly.swarm.ts.javaee.ejb.remote.RemoteEjb");
            assertThat(service.method()).isEqualTo("remote ejb method");
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }
}
