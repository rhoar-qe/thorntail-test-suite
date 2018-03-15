package org.wildfly.swarm.ts.microprofile.config.v11;

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
public class MicroProfileConfig11Test {
    @Test
    @RunAsClient
    public void test() throws IOException {
        String response = Request.Get("http://localhost:8080/").execute().returnContent().asString();
        assertThat(response).isEqualTo(""
                + "Value of app.timeout: 314159\n"
                + "Value of missing.property: it's present anyway\n"
                + "Config contains app.timeout: true\n"
                + "Config contains missing.property: false\n"
        );
    }
}
