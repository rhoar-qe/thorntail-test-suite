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
    public void injectedString() throws IOException {
        String response = Request.Get("http://localhost:8080?mode=injected-string").execute().returnContent().asString();
        assertThat(response).isEqualTo("element1,element2\\\\,element3,element41\\,element42,ele\\\\ment5");
    }

    @Test
    @RunAsClient
    public void injectedArray() throws IOException {
        String response = Request.Get("http://localhost:8080?mode=injected-array").execute().returnContent().asString();
        assertThat(response).isEqualTo("element1; element2\\; element3; element41,element42; ele\\ment5");
    }

    @Test
    @RunAsClient
    public void injectedList() throws IOException {
        String response = Request.Get("http://localhost:8080?mode=injected-list").execute().returnContent().asString();
        assertThat(response).isEqualTo("element1; element2\\; element3; element41,element42; ele\\ment5");
    }

    @Test
    @RunAsClient
    public void lookupString() throws IOException {
        String response = Request.Get("http://localhost:8080?mode=lookup-string").execute().returnContent().asString();
        assertThat(response).isEqualTo("element1,element2\\\\,element3,element41\\,element42,ele\\\\ment5");
    }

    @Test
    @RunAsClient
    public void lookupArray() throws IOException {
        String response = Request.Get("http://localhost:8080?mode=lookup-array").execute().returnContent().asString();
        assertThat(response).isEqualTo("element1; element2\\; element3; element41,element42; ele\\ment5");
    }
}
