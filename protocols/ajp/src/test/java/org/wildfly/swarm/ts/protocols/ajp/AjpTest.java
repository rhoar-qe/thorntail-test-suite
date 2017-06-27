package org.wildfly.swarm.ts.protocols.ajp;

import org.apache.http.client.fluent.Request;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kohsuke.ajp.client.SimpleAjpClient;
import org.kohsuke.ajp.client.TesterAjpMessage;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment
public class AjpTest {
    @Test
    @RunAsClient
    public void http() throws IOException, GeneralSecurityException {
        String response = Request.Get("http://localhost:8080/").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello on port 8080");
    }

    @Test
    @RunAsClient
    public void ajp() throws IOException, GeneralSecurityException {
        SimpleAjpClient client = new SimpleAjpClient();
        client.connect("localhost", 8009);
        try {
            TesterAjpMessage message = client.createForwardMessage("/");
            message.end();

            client.sendMessage(message);

            String response = client.readMessage().readString().trim();
            assertThat(response).isEqualTo("Hello on port 8009");
        } finally {
            client.disconnect();
        }
    }
}
