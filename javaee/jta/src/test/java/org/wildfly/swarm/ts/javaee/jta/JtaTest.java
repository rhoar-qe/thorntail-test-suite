package org.wildfly.swarm.ts.javaee.jta;

import org.apache.http.client.fluent.Request;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import javax.transaction.Status;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment
public class JtaTest {
    @Test
    @RunAsClient
    public void noTx() throws IOException {
        String result = Request.Get("http://localhost:8080/no-tx").execute().returnContent().asString();
        assertThat(result).isEqualTo("" + Status.STATUS_NO_TRANSACTION);
    }

    @Test
    @RunAsClient
    public void withTxCommit() throws IOException {
        String result = Request.Get("http://localhost:8080/with-tx-commit").execute().returnContent().asString();
        assertThat(result).isEqualTo(""
                + Status.STATUS_NO_TRANSACTION + "\r\n"
                + Status.STATUS_ACTIVE + "\r\n"
                + Status.STATUS_NO_TRANSACTION
        );
    }

    @Test
    @RunAsClient
    public void withTxRollback() throws IOException {
        String result = Request.Get("http://localhost:8080/with-tx-rollback").execute().returnContent().asString();
        assertThat(result).isEqualTo(""
                + Status.STATUS_NO_TRANSACTION + "\r\n"
                + Status.STATUS_ACTIVE + "\r\n"
                + Status.STATUS_MARKED_ROLLBACK + "\r\n"
                + Status.STATUS_NO_TRANSACTION
        );
    }
}
