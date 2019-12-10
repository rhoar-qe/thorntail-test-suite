package org.wildfly.swarm.ts.hollow.jar.microprofile;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.awaitility.Awaitility;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MicroProfileHollowJarIT {
    @Before
    public void setup() {
        Awaitility.setDefaultPollInterval(10, TimeUnit.MILLISECONDS);
    }

    @Test
    public void jaxrsCdiJsonp() throws IOException {
        String response = Request.Get("http://localhost:8080/basic").execute().returnContent().asString();
        JsonElement json = JsonParser.parseString(response);
        assertThat(json.isJsonObject()).isTrue();
        assertThat(json.getAsJsonObject().size()).isEqualTo(1);
        assertThat(json.getAsJsonObject().has("content")).isTrue();
        assertThat(json.getAsJsonObject().get("content").getAsString()).isEqualTo("Hello, World!");
    }

    @Test
    public void config() throws IOException {
        String response = Request.Get("http://localhost:8080/config").execute().returnContent().asString();
        assertThat(response).isEqualTo(""
                + "Value of app.timeout: 314159\n"
                + "Value of missing.property: it's present anyway\n"
                + "Config contains app.timeout: true\n"
                + "Config contains missing.property: false\n"
                + "Custom config source prop.from.config.source: TestConfigSource\n"
                + "Custom config source prop.from.config.source.with.ordinal: TestConfigSource\n"
        );
    }

    @Test
    public void faultTolerance_timeout_ok() throws IOException {
        String response = Request.Get("http://localhost:8080/fault-tolerant?operation=timeout&context=foobar").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello from @Timeout method, context = foobar");
    }

    @Test
    public void faultTolerance_timeout_failure() throws IOException {
        String response = Request.Get("http://localhost:8080/fault-tolerant?operation=timeout&context=foobar&fail=true").execute().returnContent().asString();
        assertThat(response).isEqualTo("Fallback Hello, context = foobar");
    }

    @Test
    public void faultTolerance_retry_ok() throws IOException {
        String response = Request.Get("http://localhost:8080/fault-tolerant?operation=retry&context=foobar").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello from @Retry method, context = foobar");
    }

    @Test
    public void faultTolerance_retry_failure() throws IOException {
        String response = Request.Get("http://localhost:8080/fault-tolerant?operation=retry&context=foobar&fail=true").execute().returnContent().asString();
        assertThat(response).isEqualTo("Fallback Hello, context = foobar");
    }

    @Test
    public void faultTolerance_circuitBreaker_ok() throws IOException {
        String response = Request.Get("http://localhost:8080/fault-tolerant?operation=circuit-breaker&context=foobar").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello from @CircuitBreaker method, context = foobar");
    }

    @Test
    public void faultTolerance_circuitBreaker_failure() throws IOException {
        for (int i = 0; i < 20; i++) {
            String response = Request.Get("http://localhost:8080/fault-tolerant?operation=circuit-breaker&context=foobar&fail=true").execute().returnContent().asString();
            assertThat(response).isEqualTo("Fallback Hello, context = foobar");
        }

        for (int i = 0; i < 10; i++) {
            String response = Request.Get("http://localhost:8080/fault-tolerant?operation=circuit-breaker&context=foobar").execute().returnContent().asString();
            assertThat(response).isEqualTo("Fallback Hello, context = foobar");
        }

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            String response = Request.Get("http://localhost:8080/fault-tolerant?operation=circuit-breaker&context=foobar").execute().returnContent().asString();
            assertThat(response).isEqualTo("Hello from @CircuitBreaker method, context = foobar");
        });
    }

    @Test
    public void faultTolerance_bulkhead_ok() throws InterruptedException {
        Map<String, Integer> expectedResponses = new HashMap<>();
        expectedResponses.put("Hello from @Bulkhead method, context = foobar", 10);

        // 10 allowed invocations
        // 11 invocations would already trigger fallback
        testBulkhead(10, "http://localhost:8080/fault-tolerant?operation=bulkhead&context=foobar", expectedResponses);
    }

    @Test
    public void faultTolerance_bulkhead_failure() throws InterruptedException {
        Map<String, Integer> expectedResponses = new HashMap<>();
        expectedResponses.put("Hello from @Bulkhead method, context = foobar", 10);
        expectedResponses.put("Fallback Hello, context = foobar", 10);

        // 20 = 10 allowed invocations + 10 not allowed invocations that lead to fallback
        // 21 invocations would already trigger SWARM-1946
        testBulkhead(20, "http://localhost:8080/fault-tolerant?operation=bulkhead&context=foobar&fail=true", expectedResponses);
    }

    private static void testBulkhead(int parallelRequests, String url, Map<String, Integer> expectedResponses) throws InterruptedException {
        Set<String> violations = Collections.newSetFromMap(new ConcurrentHashMap<>());
        Queue<String> seenResponses = new ConcurrentLinkedQueue<>();

        ExecutorService executor = Executors.newFixedThreadPool(parallelRequests);
        for (int i = 0; i < parallelRequests; i++) {
            executor.submit(() -> {
                try {
                    String response = Request.Get(url).execute().returnContent().asString();
                    seenResponses.add(response);
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
                violations.add("Expected to see " + expectedResponse.getValue() + " occurence(s) but seen " + count
                        + ": " + expectedResponse.getKey());
            }
        }

        assertThat(violations).isEmpty();
    }

    @Test
    public void healthCheck() throws IOException {
        String response = Request.Get("http://localhost:8080/health").execute().returnContent().asString();
        JsonElement json = JsonParser.parseString(response);
        assertThat(json.isJsonObject()).isTrue();
        assertThat(json.getAsJsonObject().has("status")).isTrue();
        assertThat(json.getAsJsonObject().get("status").getAsString()).isEqualTo("UP");
        assertThat(json.getAsJsonObject().has("checks")).isTrue();
        JsonArray checks = json.getAsJsonObject().getAsJsonArray("checks");
        assertThat(checks.size()).isEqualTo(1);
        JsonObject check = checks.get(0).getAsJsonObject();
        assertThat(check.has("name")).isTrue();
        assertThat(check.get("name").getAsString()).isEqualTo("health");
        assertThat(check.has("status")).isTrue();
        assertThat(check.get("status").getAsString()).isEqualTo("UP");
        assertThat(check.has("data")).isTrue();
        assertThat(check.get("data").getAsJsonObject().get("key").getAsString()).isEqualTo("value");
    }

    @Test
    public void metrics_1_trigger() throws IOException {
        {
            String response = Request.Get("http://localhost:8080/counted-timed-metered").execute().returnContent().asString();
            assertThat(response).isEqualTo("Hello from counted and timed and metered method");
        }

        for (int i = 0; i < 10; i++) {
            Request.Get("http://localhost:8080/counted-timed-metered").execute().discardContent();
        }
    }

    @Test
    public void metrics_2_jsonMetadata() throws IOException {
        String response = Request.Options("http://localhost:8080/metrics")
                .addHeader("Accept", "application/json").execute().returnContent().asString();
        JsonObject json = JsonParser.parseString(response).getAsJsonObject();
        assertThat(json.has("application")).isTrue();
        JsonObject app = json.getAsJsonObject("application");
        assertThat(app.has("hello-count")).isTrue();
        assertThat(app.has("hello-time")).isTrue();
        assertThat(app.has("hello-freq")).isTrue();
    }

    @Test
    public void metrics_3_jsonData() throws IOException {
        String response = Request.Get("http://localhost:8080/metrics")
                .addHeader("Accept", "application/json").execute().returnContent().asString();
        JsonObject json = JsonParser.parseString(response).getAsJsonObject();
        assertThat(json.has("application")).isTrue();
        JsonObject app = json.getAsJsonObject("application");
        assertThat(app.has("hello-count")).isTrue();
        assertThat(app.get("hello-count").getAsInt()).isEqualTo(11);
        assertThat(app.has("hello-time")).isTrue();
        assertThat(app.getAsJsonObject("hello-time").get("count").getAsInt()).isEqualTo(11);
        assertThat(app.has("hello-freq")).isTrue();
        assertThat(app.getAsJsonObject("hello-freq").get("count").getAsInt()).isEqualTo(11);
    }

    @Test
    public void metrics_4_prometheusData() throws IOException {
        String response = Request.Get("http://localhost:8080/metrics").execute().returnContent().asString();
        assertThat(response).contains("application_hello_count_total 11.0");
        assertThat(response).contains("application_hello_freq_total 11.0");
    }

    private static PrivateKey loadPrivateKey() throws IOException, GeneralSecurityException {
        byte[] bytes = Files.readAllBytes(Paths.get("target/test-classes/private-key.der"));
        return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(bytes));
    }

    private static String createToken(String... groups) throws IOException, GeneralSecurityException {
        Date now = new Date();
        Date expiration = new Date(TimeUnit.MINUTES.toMillis(10) + now.getTime());

        return Jwts.builder()
                .setIssuer("https://my.auth.server/") // iss
                .setId(UUID.randomUUID().toString()) // jti
                .setExpiration(expiration) // exp
                .setIssuedAt(now) // iat
                .setSubject("test_subject_at_example_com") // sub
                .claim("upn", "test-subject@example.com")
                .claim("groups", Arrays.asList(groups))
                .signWith(SignatureAlgorithm.RS256, loadPrivateKey())
                .compact();
    }

    private static Response request(String url, String token) throws IOException {
        return Request.Get(url).addHeader("Authorization", "Bearer " + token).execute();
    }

    @Test
    public void jwt_everyone_noGroup() throws IOException, GeneralSecurityException {
        String response = request("http://localhost:8080/secured/everyone", createToken()).returnContent().asString();
        assertThat(response).isEqualTo("Hello, test-subject@example.com, your token was issued by https://my.auth.server/, you are in groups [] and you are NOT in role superuser");
    }

    @Test
    public void jwt_everyone_viewGroup() throws IOException, GeneralSecurityException {
        String response = request("http://localhost:8080/secured/everyone", createToken("view")).returnContent().asString();
        assertThat(response).isEqualTo("Hello, test-subject@example.com, your token was issued by https://my.auth.server/, you are in groups [view] and you are NOT in role superuser");
    }

    @Test
    public void jwt_everyone_adminGroup() throws IOException, GeneralSecurityException {
        String response = request("http://localhost:8080/secured/everyone", createToken("admin")).returnContent().asString();
        assertThat(response).isEqualTo("Hello, test-subject@example.com, your token was issued by https://my.auth.server/, you are in groups [admin] and you are in role superuser");
    }

    @Test
    public void jwt_admin_noGroup() throws IOException, GeneralSecurityException {
        HttpResponse response = request("http://localhost:8080/secured/admin", createToken()).returnResponse();
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(403);
    }

    @Test
    public void jwt_admin_viewGroup() throws IOException, GeneralSecurityException {
        HttpResponse response = request("http://localhost:8080/secured/admin", createToken("view")).returnResponse();
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(403);
    }

    @Test
    public void jwt_admin_adminGroup() throws IOException, GeneralSecurityException {
        String response = request("http://localhost:8080/secured/admin", createToken("admin")).returnContent().asString();
        assertThat(response).isEqualTo("Restricted area! Admin access granted!");
    }

    @Test
    public void jwt_noone_noGroup() throws IOException, GeneralSecurityException {
        HttpResponse response = request("http://localhost:8080/secured/noone", createToken()).returnResponse();
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(403);
    }

    @Test
    public void jwt_noone_viewGroup() throws IOException, GeneralSecurityException {
        HttpResponse response = request("http://localhost:8080/secured/noone", createToken("view")).returnResponse();
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(403);
    }

    @Test
    public void jwt_noone_adminGroup() throws IOException, GeneralSecurityException {
        HttpResponse response = request("http://localhost:8080/secured/noone", createToken("admin")).returnResponse();
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(403);
    }
}
