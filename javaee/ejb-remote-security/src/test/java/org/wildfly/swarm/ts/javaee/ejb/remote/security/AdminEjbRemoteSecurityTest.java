package org.wildfly.swarm.ts.javaee.ejb.remote.security;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import javax.naming.NamingException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment
public class AdminEjbRemoteSecurityTest extends AbstractEjbRemoteSecurityTest {
    @Test
    @RunAsClient
    public void test() throws NamingException {
        doTest("wildfly-config-admin.xml", service -> {
            assertThat(service.publicMethod()).isEqualTo("public secured method, user is test-admin, admin: true");
            assertThat(service.privateMethod()).isEqualTo("private secured method");
        });
    }
}
