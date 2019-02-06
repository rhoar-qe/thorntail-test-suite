package org.wildfly.swarm.ts.javaee.jsf;

import org.apache.http.client.fluent.Request;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.undertow.WARArchive;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class JsfTest {
    // can't use `@DefaultDeployment` because content of `src/main/webapp` is never copied
    // to `target/[test-]classes`, and that's what @DefaultDeployment puts into the archive
    @Deployment
    public static Archive<?> deployment() {
        return ShrinkWrap.create(WARArchive.class)
                .addPackage(JsfTest.class.getPackage())
                .addAsWebResource(new File("src/main/webapp/index.html"))
                .addAsWebResource(new File("src/main/webapp/index.xhtml"))
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/faces-config.xml"));
    }

    @Test
    @RunAsClient
    public void test() throws IOException {
        {
            String response = Request.Get("http://localhost:8080/").execute().returnContent().asString();
            assertThat(response).contains("index.jsf");
            assertThat(response).doesNotContain("Hello from JSF");
        }

        {
            String response = Request.Get("http://localhost:8080/index.jsf").execute().returnContent().asString();
            assertThat(response).doesNotContain("index.jsf");
            assertThat(response).contains("Hello from JSF");
            assertThat(response).contains("Hey there!");
        }
    }
}
