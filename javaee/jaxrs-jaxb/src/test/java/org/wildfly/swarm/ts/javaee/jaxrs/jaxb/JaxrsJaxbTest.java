package org.wildfly.swarm.ts.javaee.jaxrs.jaxb;

import org.apache.http.client.fluent.Request;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.Diff;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment
public class JaxrsJaxbTest {
    @Test
    @RunAsClient
    public void test() throws IOException {
        String response = Request.Get("http://localhost:8080/").execute().returnContent().asString();
        Diff diff = DiffBuilder.compare(Input.fromString(response))
                .withTest(Input.fromString("<hello-element value-attribute=\"WORLD\"><value-element>WORLD</value-element></hello-element>"))
                .checkForSimilar()
                .ignoreComments()
                .ignoreWhitespace()
                .build();
        assertThat(diff.hasDifferences()).as(diff.toString()).isFalse();
    }
}
