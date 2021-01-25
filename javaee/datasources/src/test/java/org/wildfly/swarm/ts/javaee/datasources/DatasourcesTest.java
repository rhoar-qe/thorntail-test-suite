package org.wildfly.swarm.ts.javaee.datasources;

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
public class DatasourcesTest {
    @Test
    @RunAsClient
    public void test() throws IOException {
        String result = Request.Get("http://localhost:8080/").execute().returnContent().asString();
        assertThat(result).isEqualTo("DataSource present: true\r\nConnection obtained: true\r\n");
    }
}
