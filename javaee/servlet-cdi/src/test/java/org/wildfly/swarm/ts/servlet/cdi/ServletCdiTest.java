package org.wildfly.swarm.ts.servlet.cdi;

import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.impl.client.BasicCookieStore;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment
public class ServletCdiTest {
    private Executor executor1 = Executor.newInstance().use(new BasicCookieStore());
    private Executor executor2 = Executor.newInstance().use(new BasicCookieStore());
    private Request request = Request.Get("http://localhost:8080/hello?name=Ladicek");

    @Test
    @RunAsClient
    public void hello() throws IOException {
        sendRequestAndAssertCounter(executor1, 1);
        sendRequestAndAssertCounter(executor1, 2);
        sendRequestAndAssertCounter(executor2, 1);
        sendRequestAndAssertCounter(executor1, 3);
        sendRequestAndAssertCounter(executor2, 2);

        executor1.clearCookies();

        sendRequestAndAssertCounter(executor1, 1);
        sendRequestAndAssertCounter(executor2, 3);
    }

    private void sendRequestAndAssertCounter(Executor executor, int expectedCounter) throws IOException {
        String result = executor.execute(request).returnContent().asString();
        assertThat(result).isEqualTo("Hello, Ladicek! Counter: " + expectedCounter);
    }
}
