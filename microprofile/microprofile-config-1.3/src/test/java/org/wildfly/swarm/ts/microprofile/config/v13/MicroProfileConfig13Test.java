package org.wildfly.swarm.ts.microprofile.config.v13;

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
public class MicroProfileConfig13Test {
    @Test
    @RunAsClient
    public void test() throws IOException {
        String response = Request.Get("http://localhost:8080/").execute().returnContent().asString();

        assertThat(response).isEqualTo(
                "MY_ENVIRONMENT_VARIABLE: value from surefire envvar\n" +
                "MY.ENVIRONMENT.VARIABLE: value from surefire envvar\n" +
                "my.environment.variable: value from surefire envvar\n" +
                "MY_ENVIRONMENT_VARIABLE: value from surefire envvar\n" +
                "MY.ENVIRONMENT.VARIABLE: value from surefire envvar\n" +
                "my.environment.variable: value from surefire envvar\n" +
                "URI: http://microprofile.io\n" +
                "Optional notExisting: false\n" +
                "Optional existing: true\n");
    }
}
