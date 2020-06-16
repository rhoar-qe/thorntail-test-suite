package org.wildfly.swarm.ts.javaee.ejb.remote.http;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Hashtable;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment
public class EjbRemoteHttpTest {
    @Test
    @RunAsClient
    public void test() throws NamingException {
        Hashtable<String, Object> jndiProps = new Hashtable<>();
        jndiProps.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        // must be configured here, because discovery for EJB over HTTP doesn't exist in WildFly yet
        // (see https://issues.redhat.com/browse/WEJBHTTP-34 and https://issues.redhat.com/browse/WFLY-13492)
        jndiProps.put(Context.PROVIDER_URL, "http://localhost:8080/wildfly-services");

        Context ctx = null;
        try {
            ctx = new InitialContext(jndiProps);
            RemoteEjb service = (RemoteEjb) ctx.lookup("ejb:/" + this.getClass().getSimpleName()
                    + "/RemoteEjbImpl!org.wildfly.swarm.ts.javaee.ejb.remote.http.RemoteEjb");

            assertThat(service.hello()).isEqualTo("hello test-user");
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }
}
