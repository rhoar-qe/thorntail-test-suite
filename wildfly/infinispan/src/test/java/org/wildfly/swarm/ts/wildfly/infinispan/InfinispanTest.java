package org.wildfly.swarm.ts.wildfly.infinispan;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.assertj.core.api.Assertions;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;
import org.wildfly.swarm.spi.api.annotations.DeploymentModule;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment
@DeploymentModule(name = "org.infinispan") // needed for CacheInitializer to be able to inject Infinispan classes
public class InfinispanTest {
    @Test
    @RunAsClient
    public void existingKey() throws Exception {
        String response = Request.Get("http://localhost:8080/?key=foo").execute().returnContent().asString();
        assertThat(response).isEqualTo("bar");
    }

    @Test
    @RunAsClient
    public void newKey() throws Exception {
        {
            HttpResponse response = Request.Get("http://localhost:8080/?key=baz").execute().returnResponse();
            assertThat(response.getStatusLine().getStatusCode()).isEqualTo(404);
        }

        {
            HttpResponse response = Request.Post("http://localhost:8080/?key=baz&value=quux").execute().returnResponse();
            assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
        }

        {
            String response = Request.Get("http://localhost:8080/?key=baz").execute().returnContent().asString();
            assertThat(response).isEqualTo("quux");
        }

        {
            HttpResponse response = Request.Delete("http://localhost:8080/?key=baz").execute().returnResponse();
            assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
        }

        {
            HttpResponse response = Request.Get("http://localhost:8080/?key=baz").execute().returnResponse();
            assertThat(response.getStatusLine().getStatusCode()).isEqualTo(404);
        }
    }
}
