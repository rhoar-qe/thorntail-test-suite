package org.wildfly.swarm.ts.hollow.jar.web;

import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class WebHollowJarIT {
    @Test
    public void testJaxrs() throws IOException {
        String response = Request.Get("http://localhost:8080/rest/hello?name=World").execute().returnContent().asString();
        assertThat(response).isEqualTo("JAX-RS says: Hello, World!");
    }

    @Test
    public void testServlet() throws IOException {
        String response = Request.Get("http://localhost:8080/servlet?name=World").execute().returnContent().asString();
        assertThat(response).isEqualTo("Servlet says: Hello, World!\nInit param:bar");
    }

    @Test
    public void testJpa() throws IOException {
        String response = Request.Get("http://localhost:8080/jpa").execute().returnContent().asString();
        assertThat(response).isEqualTo("1: Hello from JPA servlet\n");
    }

    @Test
    public void testJsf() throws IOException {
        String response = Request.Get("http://localhost:8080/faces/index.xhtml").execute().returnContent().asString();
        assertThat(response).contains("Hello from Backing Bean");
        assertThat(response).contains("Hello from BackingBeanAnnotation");
        assertThat(response).contains("Hello from BackingBeanAnnotationCdi");
    }

    @Test
    public void testJsp() throws IOException {
        String response = Request.Get("http://localhost:8080/index.jsp").execute().returnContent().asString();
        assertThat(response).contains("Hello from JSP + JSTL");
    }

    @Test
    public void testEjb() throws IOException {
        String response = Request.Get("http://localhost:8080/ejb").execute().returnContent().asString();
        assertThat(response).isEqualTo("EJB method");
    }

    @Test
    public void testBeanValidation() throws IOException {
        Response response = Request.Get("http://localhost:8080/rest/beanvalidation?param=123").execute();
        assertThat(response.returnResponse().getStatusLine().getStatusCode()).isEqualTo(400);

        response = Request.Get("http://localhost:8080/rest/beanvalidation?param=1234567890").execute();
        assertThat(response.returnResponse().getStatusLine().getStatusCode()).isEqualTo(200);
    }
}
