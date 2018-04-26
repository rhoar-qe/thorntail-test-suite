package org.wildfly.swarm.ts.javaee.ejb.remote.security;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Hashtable;
import java.util.function.Consumer;

abstract class AbstractEjbRemoteSecurityTest {
    void doTest(String configFile, Consumer<SecuredRemoteEjb> test) throws NamingException {
        System.setProperty("wildfly.config.url", "src/test/resources/" + configFile);

        Hashtable<String, Object> jndiProps = new Hashtable<>();
        jndiProps.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        jndiProps.put(Context.PROVIDER_URL, "remote+http://localhost:8080");

        Context ctx = null;
        try {
            ctx = new InitialContext(jndiProps);
            SecuredRemoteEjb service = (SecuredRemoteEjb) ctx.lookup("ejb:/" + this.getClass().getSimpleName()
                    + "/SecuredRemoteEjbImpl!org.wildfly.swarm.ts.javaee.ejb.remote.security.SecuredRemoteEjb");
            test.accept(service);
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }
}
