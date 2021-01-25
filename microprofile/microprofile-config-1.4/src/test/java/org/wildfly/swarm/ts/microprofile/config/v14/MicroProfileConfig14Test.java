package org.wildfly.swarm.ts.microprofile.config.v14;

import java.io.IOException;

import org.apache.http.client.fluent.Request;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment
public class MicroProfileConfig14Test {

    @Test
    @RunAsClient
    public void test() throws IOException {
        String response = Request.Get("http://localhost:8080/").execute().returnContent().asString();
        assertThat(response).isEqualTo(""
                + "my.byte.property: 100\r\n"
                + "my.byte.property: 100\r\n"
                + "my.short.property: 10000\r\n"
                + "my.short.property: 10000\r\n"
                + "my.char.property: Q\r\n"
                + "my.char.property: Q\r\n"
        );
    }
}
