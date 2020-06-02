package org.wildfly.swarm.ts.microprofile.fault.tolerance.v20;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.fluent.Request;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@RunWith(Arquillian.class)
@DefaultDeployment
public class FaultTolerance20AsyncTest {
    @Test
    @RunAsClient
    public void timeoutOkCompletionStage() throws IOException {
        String response = Request.Get("http://localhost:8080/async?operation=timeout").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello from @Timeout method");
    }

    @Test
    @RunAsClient
    public void timeoutFailureCompletionStage() throws IOException {
        String response = Request.Get("http://localhost:8080/async?operation=timeout&fail=true").execute().returnContent().asString();
        assertThat(response).isEqualTo("Fallback Hello");
    }

    @Test
    @RunAsClient
    public void bulkheadTimeoutFailure() throws InterruptedException {
        Map<String, Integer> expectedResponses = new HashMap<>();
        expectedResponses.put("Hello from @Bulkhead @Timeout method", 0);
        expectedResponses.put("Fallback Hello", 50);
        // all executions will end up as a fallback -- some because they timeout,
        // some because they are rejected by the bulkhead (which allows 15 executions and 15 queued executions)
        testBulkhead(50, "http://localhost:8080/async?operation=bulkhead-timeout&fail=true", expectedResponses);
    }

    @Test
    @RunAsClient
    public void bulkheadTimeoutRetryOK() throws IOException {
        String response = Request.Get("http://localhost:8080/async?operation=bulkhead-timeout-retry").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello from @Bulkhead @Timeout @Retry method");
    }

    @Test
    @RunAsClient
    public void bulkheadTimeoutRetryFailure() throws InterruptedException {
        Map<String, Integer> expectedResponses = new HashMap<>();
        expectedResponses.put("Hello from @Bulkhead @Timeout @Retry method", 0);
        expectedResponses.put("Fallback Hello", 50);

        testBulkhead(50, "http://localhost:8080/async?operation=bulkhead-timeout-retry&fail=true", expectedResponses);
    }

    private static void testBulkhead(int parallelRequests, String url, Map<String, Integer> expectedResponses) throws InterruptedException {
        Set<String> violations = Collections.newSetFromMap(new ConcurrentHashMap<>());
        Queue<String> seenResponses = new ConcurrentLinkedQueue<>();

        ExecutorService executor = Executors.newFixedThreadPool(parallelRequests);
        for (int i = 0; i < parallelRequests; i++) {
            executor.submit(() -> {
                try {
                    seenResponses.add(Request.Get(url).execute().returnContent().asString());
                } catch (Exception e) {
                    violations.add("Unexpected exception: " + e.getMessage());
                }
            });
        }
        executor.shutdown();
        boolean finished = executor.awaitTermination(10, TimeUnit.SECONDS);
        assertThat(finished).isTrue();

        for (String seenResponse : seenResponses) {
            if (!expectedResponses.containsKey(seenResponse)) {
                violations.add("Unexpected response: " + seenResponse);
            }
        }
        for (Map.Entry<String, Integer> expectedResponse : expectedResponses.entrySet()) {
            int count = 0;
            for (String seenResponse : seenResponses) {
                if (expectedResponse.getKey().equals(seenResponse)) {
                    count++;
                }
            }
            if (count != expectedResponse.getValue()) {
                violations.add("Expected to see " + expectedResponse.getValue() + " occurrence(s) but seen " + count
                                       + ": " + expectedResponse.getKey());
            }
        }
        assertThat(violations).isEmpty();
    }

    @Test
    @RunAsClient
    public void retryCircuitBreakerFailure() throws IOException, InterruptedException {
        testCircuitBreakerFailure("http://localhost:8080/async?operation=retry-circuit-breaker",
                                  "Fallback Hello",
                                  "Hello from @Retry @CircuitBreaker method");
    }

    @Test
    @RunAsClient
    public void retryCircuitBreakerTimeoutOK() throws IOException {
        String response = Request.Get("http://localhost:8080/async?operation=retry-circuit-breaker-timeout").execute().returnContent().asString();//
        assertThat(response).isEqualTo("Hello from @Retry @CircuitBreaker @Timeout method");
    }

    @Test
    @RunAsClient
    public void retryCircuitBreakerTimeoutFailure() throws IOException, InterruptedException {
        testCircuitBreakerFailure("http://localhost:8080/async?operation=retry-circuit-breaker-timeout",
                                  "Fallback Hello",
                                  "Hello from @Retry @CircuitBreaker @Timeout method");
    }

    private static void testCircuitBreakerFailure(String url, String expectedFallbackResponse, String expectedOkResponse) throws IOException, InterruptedException {
        // call 20x fail URL, circuit breaker is OPEN
        for (int i = 0; i < 20; i++) {
            String response = Request.Get(url + "&fail=true").execute().returnContent().asString();
            assertThat(response).isEqualTo(expectedFallbackResponse);
        }
        // call 10x correct URL on open circuit breaker -> still returns fallback response
        for (int i = 0; i < 10; i++) {
            String response = Request.Get(url).execute().returnContent().asString();
            assertThat(response).isEqualTo(expectedFallbackResponse);
        }
        // the window of 20 calls now contains 10 fail and 10 correct responses, this equals 0.5 failureRatio
        // @CircuitBreaker.delay is 5 seconds, then circuit breaker is CLOSED and OK response is returned
        Thread.sleep(5000L);
        await().atMost(1, TimeUnit.SECONDS).untilAsserted(() -> {
            String response = Request.Get(url).execute().returnContent().asString();
            assertThat(response).isEqualTo(expectedOkResponse);
        });
    }

    @Test
    @RunAsClient
    public void retryTimeout() throws IOException {
        String response = Request.Get("http://localhost:8080/async?operation=retry-timeout&fail=true").execute().returnContent().asString();
        assertThat(response).isEqualTo("Fallback Hello");
    }

    @Test
    @RunAsClient
    public void timeoutCircuitBreakerOK() throws IOException {
        String response = Request.Get("http://localhost:8080/async?operation=timeout-circuit-breaker").execute().returnContent().asString();//
        assertThat(response).isEqualTo("Hello from @Timeout @CircuitBreaker method");
    }

    @Test
    @RunAsClient
    public void timeoutCircuitBreakerFailure() throws IOException, InterruptedException {
        testCircuitBreakerFailure("http://localhost:8080/async?operation=timeout-circuit-breaker",
                                  "Fallback Hello",
                                  "Hello from @Timeout @CircuitBreaker method");
    }
}
