package org.wildfly.swarm.ts.javaee.jsp.jstl;

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
public class JspJstlTest {
    // can't use `@DefaultDeployment` because content of `src/main/webapp` is never copied
    // to `target/[test-]classes`, and that's what @DefaultDeployment puts into the archive
    @Deployment
    public static Archive<?> deployment() {
        return ShrinkWrap.create(WARArchive.class)
                .addAsWebResource(new File("src/main/webapp/index.jsp"));
    }

    @Test
    @RunAsClient
    public void test() throws IOException {
        String response = Request.Get("http://localhost:8080/").execute().returnContent().asString();
        assertThat(response).contains("JSP + JSTL Test");
        assertThat(response).contains("Hello from JSP + JSTL");
    }
}
