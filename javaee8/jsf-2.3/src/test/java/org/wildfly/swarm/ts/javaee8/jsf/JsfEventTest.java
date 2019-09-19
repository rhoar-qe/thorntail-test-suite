package org.wildfly.swarm.ts.javaee8.jsf;

import org.apache.http.client.fluent.Request;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.ts.javaee8.jsf.events.EventBean;
import org.wildfly.swarm.undertow.WARArchive;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(Arquillian.class)
public class JsfEventTest {
    @Deployment
    public static Archive<?> deployment() {
        return ShrinkWrap.create(WARArchive.class)
                .addClasses(ApplicationInit.class, EventBean.class)
                .addAsWebResource(new File("src/main/webapp/event_postrenderview.xhtml"))
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/faces-config.xml"))
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/web.xml"))
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    @RunAsClient
    public void testPostRenderViewEvent() throws IOException {
        String response = Request.Get("http://localhost:8080/event_postrenderview.jsf").execute().returnContent().asString();
        assertThat(response).contains("before");
        response = Request.Get("http://localhost:8080/event_postrenderview.jsf").execute().returnContent().asString();
        assertThat(response).contains("after");
    }
}
