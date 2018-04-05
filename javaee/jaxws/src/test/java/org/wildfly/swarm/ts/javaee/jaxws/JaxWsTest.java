package org.wildfly.swarm.ts.javaee.jaxws;

import org.apache.http.client.fluent.Request;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment
public class JaxWsTest {
    @Test
    @RunAsClient
    public void testJaxWS() throws IOException {
        String response = Request.Get("http://localhost:8080/TestWebService?wsdl").execute().returnContent().asString();
        assertThat(response).contains("TestWebService").contains("webMethod");
    }
}
