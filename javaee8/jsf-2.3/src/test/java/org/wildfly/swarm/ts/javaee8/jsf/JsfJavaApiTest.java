package org.wildfly.swarm.ts.javaee8.jsf;

import java.io.File;
import java.io.IOException;

import org.apache.http.client.fluent.Request;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.ts.javaee8.jsf.javaapi.JavaApiBean;
import org.wildfly.swarm.ts.javaee8.jsf.javaapi.MyCollectionModel;
import org.wildfly.swarm.undertow.WARArchive;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class JsfJavaApiTest {
    @Deployment
    public static Archive<?> deployment() {
        return ShrinkWrap.create(WARArchive.class)
                .addClasses(ApplicationInit.class, JavaApiBean.class, MyCollectionModel.class)
                .addAsWebResource(new File("src/main/webapp/javaapi.xhtml"))
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/faces-config.xml"))
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/web.xml"));
    }

    @Test
    @RunAsClient
    public void displayIterableInDataTable() throws IOException {
        String response = Request.Get("http://localhost:8080/javaapi.jsf").execute().returnContent().asString();
        assertThat(response).contains("<td>1</td>");
        assertThat(response).contains("<td>2</td>");
    }

    @Test
    @RunAsClient
    public void displayIterableInRepeat() throws IOException {
        String response = Request.Get("http://localhost:8080/javaapi.jsf").execute().returnContent().asString();
        assertThat(response).contains("<li>1</li>");
        assertThat(response).contains("<li>2</li>");
    }

    @Test
    @RunAsClient
    public void displayMapInDataTable() throws IOException {
        String response = Request.Get("http://localhost:8080/javaapi.jsf").execute().returnContent().asString();
        assertThat(response).contains("<td>key1</td>");
        assertThat(response).contains("<td>value1</td>");
    }

    @Test
    @RunAsClient
    public void displayMapInRepeat() throws IOException {
        String response = Request.Get("http://localhost:8080/javaapi.jsf").execute().returnContent().asString();
        assertThat(response).contains("<li>key1:value1</li>");
    }

    @Test
    @RunAsClient
    public void customDataModelInDataTable() throws IOException {
        String response = Request.Get("http://localhost:8080/javaapi.jsf").execute().returnContent().asString();
        assertThat(response).contains("<td>a</td>");
        assertThat(response).contains("<td>b</td>");
    }
}
