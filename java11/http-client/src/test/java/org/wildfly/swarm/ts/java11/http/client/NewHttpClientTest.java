package org.wildfly.swarm.ts.java11.http.client;

import org.apache.http.client.fluent.Request;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;
import org.wildfly.swarm.undertow.WARArchive;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class NewHttpClientTest {
    // can't use `@DefaultDeployment` because content of `src/main/webapp` is never copied
    // to `target/[test-]classes`, and that's what @DefaultDeployment puts into the archive
    @Deployment
    public static Archive<?> deployment() {
        return ShrinkWrap.create(WARArchive.class)
                .addPackage(NewHttpClientTest.class.getPackage())
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/jboss-deployment-structure.xml"));
    }

    @Test
    @RunAsClient
    public void test() throws IOException {
        String response = Request.Get("http://localhost:8080/client").execute().returnContent().asString();
        assertThat(response).isEqualTo("New Java HTTP Client got: Hello from servlet");
    }
}
