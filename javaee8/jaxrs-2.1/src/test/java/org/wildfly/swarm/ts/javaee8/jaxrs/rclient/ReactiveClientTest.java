package org.wildfly.swarm.ts.javaee8.jaxrs.rclient;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.rxjava2.FlowableRxInvoker;
import org.jboss.resteasy.rxjava2.ObservableRxInvoker;
import org.jboss.resteasy.rxjava2.SingleRxInvoker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment
public class ReactiveClientTest {
    @Test
    @RunAsClient
    public void completionStage() throws ExecutionException, InterruptedException {
        Client client = ClientBuilder.newClient();

        try {
            CompletionStage<String> response = client.target("http://localhost:8080/")
                    .request()
                    .rx()
                    .get(String.class);

            assertThat(response.toCompletableFuture().get().trim()).isEqualTo("data: Hello from JAX-RS 2.1");
        } finally {
            client.close();
        }
    }

    @Test
    @RunAsClient
    public void single() {
        Client client = ClientBuilder.newClient();

        try {
            Single<String> response = client.target("http://localhost:8080/")
                    .request()
                    .rx(SingleRxInvoker.class)
                    .get(String.class);

            assertThat(response.blockingGet().trim()).isEqualTo("data: Hello from JAX-RS 2.1");
        } finally {
            client.close();
        }
    }

    @Test
    @RunAsClient
    public void observable() {
        Client client = ClientBuilder.newClient();

        try {
            Observable<?> response = client.target("http://localhost:8080/")
                    .request()
                    .rx(ObservableRxInvoker.class)
                    .get(String.class);

            assertThat(response.blockingFirst()).isEqualTo("Hello from JAX-RS 2.1");
        } finally {
            client.close();
        }
    }

    @Test
    @RunAsClient
    public void flowable() {
        Client client = ClientBuilder.newClient();

        try {
            Flowable<?> response = client.target("http://localhost:8080/")
                    .request()
                    .rx(FlowableRxInvoker.class)
                    .get(String.class);

            assertThat(response.blockingFirst()).isEqualTo("Hello from JAX-RS 2.1");
        } finally {
            client.close();
        }
    }
}
