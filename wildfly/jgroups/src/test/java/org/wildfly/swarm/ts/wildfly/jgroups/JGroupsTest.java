package org.wildfly.swarm.ts.wildfly.jgroups;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jgroups.JChannel;
import org.jgroups.View;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;
import org.wildfly.swarm.spi.api.annotations.DeploymentModule;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment(type = DefaultDeployment.Type.JAR)
@DeploymentModule(name = "org.jgroups") // needed for ClusterCreator to be able to inject a JGroups class
public class JGroupsTest {
    @Test
    @RunAsClient
    public void test() throws Exception {
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("jgroups.bind_addr", "127.0.0.1");

        try (JChannel channel = new JChannel("jgroups-udp.xml")) {
            channel.connect("swarm-jgroups");
            View view = channel.getView();
            assertThat(view.getMembers()).hasSize(2);
        }
    }
}
