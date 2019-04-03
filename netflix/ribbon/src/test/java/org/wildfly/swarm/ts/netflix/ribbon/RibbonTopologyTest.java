package org.wildfly.swarm.ts.netflix.ribbon;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.http.client.fluent.Request;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;
import org.wildfly.swarm.spi.api.annotations.DeploymentModule;

@RunWith(Arquillian.class)
@DefaultDeployment
@DeploymentModule(name = "org.wildfly.swarm.topology", slot = "runtime") // needed for mock topology connector
@DeploymentModule(name = "org.jboss.as.network")                         // needed for mock topology connector
public class RibbonTopologyTest {
    @Test
    @RunAsClient
    public void test() throws Exception {
        String response = Request.Get("http://localhost:8080/test").execute().returnContent().asString();
        assertThat(response).contains("Hello, World!");

        Request.Post("http://localhost:8080/hello/disable").execute().discardContent();

        response = Request.Get("http://localhost:8080/test").execute().returnContent().asString();
        assertThat(response).contains("Fallback string");
    }
}
