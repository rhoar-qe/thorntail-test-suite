package org.wildfly.swarm.ts.microprofile.fault.tolerance.v21;

import java.io.IOException;

import org.apache.http.client.fluent.Request;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@RunWith(Arquillian.class)
@DefaultDeployment
public class FallbackTest {

    private static final String FALLBACK_HELLO = "Fallback Hello";

    @Test // no fallback, throwing Exception causes expected fail
    @RunAsClient
    @InSequence(1)
    public void exception() throws IOException {
        testFallback("http://localhost:8080/fallback?operation=exception",
                     "Hello from exception", true);
    }

    @Test // no fallback, throwing MyException causes expected fail
    @RunAsClient
    @InSequence(2)
    public void myException() throws IOException {
        testFallback("http://localhost:8080/fallback?operation=myexception",
                     "Hello from myException", true);
    }

    @Test // skipOn has no effect, fallback applies
    @RunAsClient
    @InSequence(3)
    public void exception_skipOnMyE() throws IOException {
        testFallback("http://localhost:8080/fallback?operation=exception-skiponmye",
                     "Hello from exception_skipOnMyE", false);
    }

    @Test // skipOn has no effect, fallback applies
    @RunAsClient
    @InSequence(4)
    public void exception_applyOnE_skipOnMyE() throws IOException {
        testFallback("http://localhost:8080/fallback?operation=exception-applyone-skiponmye",
                     "Hello from exception_applyOnE_skipOnMyE", false);
    }

    @Test // skipOn MyException UNBLOCKS MyException, fallback is not applied
    @RunAsClient
    @InSequence(5)
    public void myException_applyOnE_skipOnMyE() throws IOException {
        testFallback("http://localhost:8080/fallback?operation=myexception-applyone-skiponmye",
                     "Hello from myException_applyOnE_skipOnMyE", true);
    }

    @Test // skipOn Exception UNBLOCKS Exception, fallback is not applied
    @RunAsClient
    @InSequence(6)
    public void exception_applyOnMyE_skipOnE() throws IOException {
        testFallback("http://localhost:8080/fallback?operation=exception-applyonmye-skipone",
                     "Hello from exception_applyOnMyE_skipOnE", true);
    }

    // skipOn Exception takes effect on MyException as it is ancestor, fallback is not applied
    // Note: skipOn has higher priority over applyOn
    @Test
    @RunAsClient
    @InSequence(7)
    public void myException_applyOnMyE_skipOnE() throws IOException {
        testFallback("http://localhost:8080/fallback?operation=myexception-applyonmye-skipone",
                     "Hello from myException_applyOnMyE_skipOnE", true);
    }

    private static void testFallback(String url, String expectedOkResponse, boolean expectingFail) throws IOException {
        String response = Request.Get(url).execute().returnContent().asString();
        assertThat(response).isEqualTo(expectedOkResponse);

        boolean exceptionOccurred = false;
        final String failUrl = url + "&fail=true";
        try {
            response = Request.Get(failUrl).execute().returnContent().asString();
            assertThat(response).isEqualTo(FALLBACK_HELLO);
        } catch (Exception e) {
            exceptionOccurred = true;
        }

        if (expectingFail) {
            if (!exceptionOccurred) {
                fail("Request to '" + failUrl + "' should cause exception! But response: " + response);
            }
        } else {
            if (exceptionOccurred) {
                fail("Request to '" + failUrl + "' should NOT cause exception!");
            }
        }
    }
}
