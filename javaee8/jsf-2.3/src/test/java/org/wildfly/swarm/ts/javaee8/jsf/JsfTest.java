package org.wildfly.swarm.ts.javaee8.jsf;

import org.apache.http.client.fluent.Request;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.ts.javaee8.jsf.general.Entity;
import org.wildfly.swarm.ts.javaee8.jsf.general.SecondEntity;
import org.wildfly.swarm.undertow.WARArchive;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class JsfTest {
    @Deployment
    public static Archive<?> deployment() {
        return ShrinkWrap.create(WARArchive.class)
                .addClasses(ApplicationInit.class, Entity.class, SecondEntity.class)
                .addAsWebResource(new File("src/main/webapp/index.xhtml"))
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/faces-config.xml"))
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/web.xml"));
    }

    @Test
    @RunAsClient
    public void test() throws IOException {
        String response = Request.Get("http://localhost:8080/index.jsf").execute().returnContent().asString();
        assertThat(response).as("java8 date formatting").contains("12/21/2012 00:00");
        assertThat(response).as("managed property injection").contains("12-21-2012");
    }
}
