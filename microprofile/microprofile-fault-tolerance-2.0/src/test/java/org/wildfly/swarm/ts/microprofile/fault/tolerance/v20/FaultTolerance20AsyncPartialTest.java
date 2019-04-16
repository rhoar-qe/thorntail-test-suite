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

import com.google.common.collect.Range;
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
public class FaultTolerance20AsyncPartialTest {

    /**
     * Test sends 40 parallel requests. There are annotations on service:
     * timeout 1s and bulkhead (of maximum number of concurrent calls = 15 and 5 queued).
     * Every even call is made to timeout. Calls above 20 fall-back.
     * Tests expects maximal success of 20 messages (those not timeout-ed + not fallback-ed).
     */
    @Test
    @RunAsClient
    public void bulkhead15q5Timeout() throws InterruptedException {
        Map<String, Range<Integer>> expectedResponses = new HashMap<>();
        expectedResponses.put("Hello from @Bulkhead @Timeout method", Range.closed(0, 20));
        expectedResponses.put("Fallback Hello", Range.closed(20, 40));
        testPartial(40, "http://localhost:8080/partial?operation=bulkhead15q5-timeout&counter=", expectedResponses);
    }

    /**
     * Test sends 40 parallel requests. There are annotations on service:
     * timeout 1s and bulkhead (of maximum number of concurrent calls = 5 and 5 queued).
     * Every even call is made to timeout. Calls above 20 fall-back.
     * Tests expects maximal success of 20 messages (those not timeout-ed + not fallback-ed).
     */
    @Test
    @RunAsClient
    public void bulkhead5q5Timeout() throws InterruptedException {
        Map<String, Range<Integer>> expectedResponses = new HashMap<>();
        expectedResponses.put("Hello from @Bulkhead @Timeout method", Range.closed(0, 20));
        expectedResponses.put("Fallback Hello", Range.closed(20, 40));
        testPartial(40, "http://localhost:8080/partial?operation=bulkhead5q5-timeout&counter=", expectedResponses);
    }

    /**
     * Test sends 20 parallel requests. There are annotations on service:
     * timeout 1s, bulkhead (of maximum number of concurrent calls = 5 and 5 queued), retry
     * 5 requests invoke timeout exception immediately, 5 requests time-out. Calls above 10 fall-back.
     * Tests expects maximal success of 10 messages (those without exception, not timeout-ed + not fallback-ed).
     */
    @Test
    @RunAsClient
    public void bulkhead5q5TimeoutRetry() throws InterruptedException {
        Map<String, Range<Integer>> expectedResponses = new HashMap<>();
        expectedResponses.put("Hello from @Bulkhead @Timeout @Retry method", Range.closed(0, 10));
        expectedResponses.put("Fallback Hello", Range.closed(10, 20));
        testPartial(20, "http://localhost:8080/partial?operation=bulkhead5q5-timeout-retry&counter=", expectedResponses);
    }

    /**
     * Test sends 20 parallel requests. There are annotations on service:
     * timeout 1s, bulkhead (of maximum number of concurrent calls = 15 and 5 queued), retry
     * 5 requests invoke timeout exception immediately, 5 requests time-out.
     * Tests expects maximal success of 10 messages (those without exception, not timeout-ed).
     */
    @Test
    @RunAsClient
    public void bulkhead15q5TimeoutRetry() throws InterruptedException {
        Map<String, Range<Integer>> expectedResponses = new HashMap<>();
        expectedResponses.put("Hello from @Bulkhead @Timeout @Retry method", Range.closed(0, 10));
        expectedResponses.put("Fallback Hello", Range.closed(10, 20));
        testPartial(20, "http://localhost:8080/partial?operation=bulkhead15q5-timeout-retry&counter=", expectedResponses);
    }

    private static void testPartial(int parallelRequests, String url, Map<String, Range<Integer>> expectedResponses) throws InterruptedException {
        Set<String> violations = Collections.newSetFromMap(new ConcurrentHashMap<>());
        Queue<String> seenResponses = new ConcurrentLinkedQueue<>();

        ExecutorService executor = Executors.newFixedThreadPool(parallelRequests);
        for (int i = 0; i < parallelRequests; i++) {
            final int finalI = i;
            executor.submit(() -> {
                try {
                    seenResponses.add(Request.Get(url + finalI).execute().returnContent().asString());
                } catch (Exception e) {
                    violations.add("Unexpected exception: " + e.getMessage());
                }
            });
        }
        executor.shutdown();
        boolean finished = executor.awaitTermination(15, TimeUnit.SECONDS);
        assertThat(finished).isTrue();

        for (String seenResponse : seenResponses) {
            if (!expectedResponses.containsKey(seenResponse.replaceAll("[0-9]", ""))) {
                violations.add("Unexpected response: " + seenResponse);
            }
        }
        for (Map.Entry<String, Range<Integer>> expectedResponse : expectedResponses.entrySet()) {
            int count = 0;
            for (String seenResponse : seenResponses) {
                if (expectedResponse.getKey().equals(seenResponse.replaceAll("[0-9]", ""))) {
                    count++;
                }
            }
            if (!expectedResponse.getValue().contains(count)) {
                violations.add("Expected to see " + expectedResponse.getValue() + " occurrence(s) but seen " + count
                                       + ": " + expectedResponse.getKey());
            }
        }
        assertThat(violations).isEmpty();
    }

    /**
     * Test sends 16 parallel requests. There are annotations on service:
     * Retry(retryOn = IOException.class), CircuitBreaker(failOn = IOException.class,
     * requestVolumeThreshold = 5, successThreshold = 3, delay = 2, delayUnit = ChronoUnit.SECONDS, failureRatio = 0.75)
     * 4 requests pass, 12 invoke IOException.
     * After that the circuit is open, after at most 3 seconds it is closed.
     */
    @Test
    @RunAsClient
    public void retryCircuitBreaker() throws InterruptedException, IOException {
        Map<String, Range<Integer>> expectedResponses = new HashMap<>();
        expectedResponses.put("Hello from @Retry @CircuitBreaker method", Range.closed(0, 4));
        expectedResponses.put("Fallback Hello", Range.closed(12, 16));
        testPartial(16, "http://localhost:8080/partial?operation=retry-circuitbreaker&counter=", expectedResponses);

        // ensure circuit is opened (note number 88 does request correct behavior!)
        String response2 = Request.Get("http://localhost:8080/partial?operation=retry-circuitbreaker&counter=88").execute().returnContent().asString();
        assertThat(response2).isEqualTo("Fallback Hello88");
        // ensure circuit is closed
        await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
            String response = Request.Get("http://localhost:8080/partial?operation=retry-circuitbreaker&counter=88").execute().returnContent().asString();
            assertThat(response).isEqualTo("Hello from @Retry @CircuitBreaker method88");
        });
    }

    /**
     * Test sends 20 parallel requests. There are annotations on service:
     * Retry(maxRetries = 2), CircuitBreaker(failOn = TimeoutException.class), Timeout
     * 5 requests pass, 10 invoke TimeoutException, 5 requests time-out.
     * After that the circuit is open, after at most 6 seconds it is closed.
     */
    @Test
    @RunAsClient
    public void retryCircuitBreakerTimeout() throws InterruptedException, IOException {
        Map<String, Range<Integer>> expectedResponses = new HashMap<>();
        expectedResponses.put("Hello from @Retry @CircuitBreaker @Timeout method", Range.closed(0, 5));
        expectedResponses.put("Fallback Hello", Range.closed(15, 20));
        testPartial(20, "http://localhost:8080/partial?operation=retry-circuitbreaker-timeout&counter=", expectedResponses);

        // ensure circuit is opened (note number 99 does request correct behavior!)
        String response2 = Request.Get("http://localhost:8080/partial?operation=retry-circuitbreaker-timeout&counter=99").execute().returnContent().asString();
        assertThat(response2).isEqualTo("Fallback Hello99");
        // ensure circuit is closed
        await().atMost(6000, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            String response = Request.Get("http://localhost:8080/partial?operation=retry-circuitbreaker-timeout&counter=99").execute().returnContent().asString();
            assertThat(response).isEqualTo("Hello from @Retry @CircuitBreaker @Timeout method99");
        });
    }
}
