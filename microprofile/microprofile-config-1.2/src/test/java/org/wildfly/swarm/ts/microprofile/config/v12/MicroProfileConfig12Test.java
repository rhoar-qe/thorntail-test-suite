package org.wildfly.swarm.ts.microprofile.config.v12;

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
public class MicroProfileConfig12Test {
    @Test
    @RunAsClient
    public void programmatic() throws IOException {
        String response = Request.Get("http://localhost:8080?programmatic=true").execute().returnContent().asString();
        assertThat(response).isEqualTo("programmatic: [element1, element2, element3, element41\\,element42]\n");
    }

    @Test
    @RunAsClient
    public void injected() throws IOException {
        String response = Request.Get("http://localhost:8080?programmatic=false").execute().returnContent().asString();
        assertThat(response).isEqualTo("injected: element1,element2,element3,element41\\,element42\n");
    }
}
