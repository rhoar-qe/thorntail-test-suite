package org.wildfly.swarm.ts.microprofile.fault.tolerance.v21;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.fluent.Request;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@RunWith(Arquillian.class)
@DefaultDeployment
public class CircuitBreakerTest {

    private static final String FALLBACK_HELLO = "Fallback Hello";

    @Test
    @RunAsClient
    @InSequence(1)
    public void exception_failOnE() throws IOException, InterruptedException {
        testCircuitBreaker("http://localhost:8080/circuitbreaker?operation=exception-failone",
                           20, 10, FALLBACK_HELLO,
                           "Hello from exception_failOnE");
    }

    @Test // failsOn takes only MyException, so Exception goes through (to fallback), 21th call is OK
    @RunAsClient
    @InSequence(2)
    public void exception_failOnMyE() throws IOException, InterruptedException {
        testCircuitBreaker("http://localhost:8080/circuitbreaker?operation=exception-failonmye",
                           20, 0, FALLBACK_HELLO,
                           "Hello from exception_failOnMyE");
    }

    @Test
    @RunAsClient
    @InSequence(3)
    public void myException_failOnE() throws IOException, InterruptedException {
        testCircuitBreaker("http://localhost:8080/circuitbreaker?operation=myexception-failone",
                           20, 10, FALLBACK_HELLO,
                           "Hello from myException_failOnE");
    }

    @Test
    @RunAsClient
    @InSequence(4)
    public void myException_failOnMyE() throws IOException, InterruptedException {
        testCircuitBreaker("http://localhost:8080/circuitbreaker?operation=myexception-failonmye",
                           20, 10, FALLBACK_HELLO,
                           "Hello from myException_failOnMyE");
    }

    @Test // no circuit breaker -> just fallback, 21th call is OK
    @RunAsClient
    @InSequence(5)
    public void myException_noCB() throws IOException, InterruptedException {
        testCircuitBreaker("http://localhost:8080/circuitbreaker?operation=myexception-nocb",
                           20, 0, FALLBACK_HELLO,
                           "Hello from myException_noCB");
    }

    @Test
    @RunAsClient
    @InSequence(6)
    public void myException_blankCB() throws IOException, InterruptedException {
        testCircuitBreaker("http://localhost:8080/circuitbreaker?operation=myexception-blankcb",
                           20, 10, FALLBACK_HELLO,
                           "Hello from myException_blankCB");
    }

    @Test
    @RunAsClient
    @InSequence(7)
    public void exception_failOnE_skipOnMyE() throws IOException, InterruptedException {
        testCircuitBreaker("http://localhost:8080/circuitbreaker?operation=exception-failone-skiponmye",
                           20, 10, FALLBACK_HELLO,
                           "Hello from exception_failOnE_skipOnMyE");
    }

    @Test // MyException is skipped by skipOn MyException, 21th call is OK
    @RunAsClient
    @InSequence(8)
    public void myException_failOnE_skipOnMyE() throws IOException, InterruptedException {
        testCircuitBreaker("http://localhost:8080/circuitbreaker?operation=myexception-failone-skiponmye",
                           20, 0, FALLBACK_HELLO,
                           "Hello from myException_failOnE_skipOnMyE");
    }

    @Test
    @RunAsClient
    @InSequence(9)
    public void exception_skipOnMyE() throws IOException, InterruptedException {
        testCircuitBreaker("http://localhost:8080/circuitbreaker?operation=exception-skiponmye",
                           20, 10, FALLBACK_HELLO,
                           "Hello from exception_skipOnMyE");
    }

    @Test // skipOp MyException causes only fallback to take effect, 21th call is OK
    @RunAsClient
    @InSequence(10)
    public void myException_skipOnMyE() throws IOException, InterruptedException {
        testCircuitBreaker("http://localhost:8080/circuitbreaker?operation=myexception-skiponmye",
                           20, 0, FALLBACK_HELLO,
                           "Hello from myException_skipOnMyE");
    }

    @Test // Exception is skipped by skipOn Exception, 21th call is OK
    @RunAsClient
    @InSequence(11)
    public void exception_failOnMyE_skipOnE() throws IOException, InterruptedException {
        testCircuitBreaker("http://localhost:8080/circuitbreaker?operation=exception-failonmye-skipone",
                           20, 0, FALLBACK_HELLO,
                           "Hello from exception_failOnMyE_skipOnE");
    }

    @Test // MyException is skipped by skipOn Exception, 21th call is OK
    @RunAsClient
    @InSequence(12)
    public void myException_failOnMyE_skipOnE() throws IOException, InterruptedException {
        testCircuitBreaker("http://localhost:8080/circuitbreaker?operation=myexception-failonmye-skipone",
                           20, 0, FALLBACK_HELLO,
                           "Hello from myException_failOnMyE_skipOnE");
    }

    private static void testCircuitBreaker(String url, int failCalls, int okCalls, String expectedFallbackResponse, String expectedOkResponse) throws IOException, InterruptedException {
        // call e.g. 20x fail URL, circuit breaker is OPEN
        for (int i = 0; i < failCalls; i++) {
            String response = Request.Get(url + "&fail=true").execute().returnContent().asString();
            assertThat(response).isEqualTo(expectedFallbackResponse);
        }
        // call e.g. 10x correct URL on open circuit breaker -> still returns fallback response
        for (int i = 0; i < okCalls; i++) {
            String response = Request.Get(url).execute().returnContent().asString();
            assertThat(response).isEqualTo(expectedFallbackResponse);
        }
        // the window of 20 calls now contains 10 fail and 10 correct responses, this equals 0.5 failureRatio
        // @CircuitBreaker.delay is 5 seconds, then circuit breaker is CLOSED and OK response is returned
        await().atMost(5 + 1, TimeUnit.SECONDS).untilAsserted(() -> {
            String response = Request.Get(url).execute().returnContent().asString();
            assertThat(response).isEqualTo(expectedOkResponse);
        });
    }
}
