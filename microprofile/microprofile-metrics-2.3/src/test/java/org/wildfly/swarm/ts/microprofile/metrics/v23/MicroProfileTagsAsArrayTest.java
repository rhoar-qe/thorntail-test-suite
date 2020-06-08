package org.wildfly.swarm.ts.microprofile.metrics.v23;

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
public class MicroProfileTagsAsArrayTest {

    @Test
    @RunAsClient
    public void trigger() throws IOException {
        String response = Request.Get("http://localhost:8080/tags-as-array").execute().returnContent().asString();
        assertThat(response).isEqualTo("tag1:value1");
    }
}
