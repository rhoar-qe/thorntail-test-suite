package org.wildfly.swarm.ts.microprofile.metrics.v10;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.apache.http.client.fluent.Request;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import java.io.IOException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
@DefaultDeployment
public class MicroProfileMetrics10Test {
    @Test
    @RunAsClient
    @InSequence(1)
    public void applicationMetricsAreRegisteredAtDeploymentTime() throws IOException {
        String response = Request.Options("http://localhost:8080/metrics")
                .addHeader("Accept", "application/json").execute().returnContent().asString();
        JsonObject json = new JsonParser().parse(response).getAsJsonObject();
        // SWARM-1897
        assertThat(json.has("application")).isFalse();
/*
        assertThat(json.has("application")).isTrue();
        JsonObject app = json.getAsJsonObject("application");
        assertThat(app.has("hello-count")).isTrue();
        assertThat(app.has("hello-time")).isTrue();
        assertThat(app.has("hello-freq")).isTrue();
*/
    }

    @Test
    @RunAsClient
    @InSequence(2)
    public void trigger() throws IOException {
        String response = Request.Get("http://localhost:8080/").execute().returnContent().asString();
        assertThat(response).isEqualTo("Hello from counted and timed and metered method");

        for (int i = 0; i < 10; i++) {
            Request.Get("http://localhost:8080/").execute().discardContent();
        }
    }

    @Test
    @RunAsClient
    @InSequence(3)
    public void jsonMetadata() throws IOException {
        String response = Request.Options("http://localhost:8080/metrics")
                .addHeader("Accept", "application/json").execute().returnContent().asString();
        JsonObject json = new JsonParser().parse(response).getAsJsonObject();
        assertThat(json.has("application")).isTrue();
        JsonObject app = json.getAsJsonObject("application");
        assertThat(app.has("hello-count")).isTrue();
        assertThat(app.getAsJsonObject("hello-count").get("displayName").getAsString()).isEqualTo("Hello Count");
        assertThat(app.getAsJsonObject("hello-count").has("description")).isTrue();
        assertThat(app.getAsJsonObject("hello-count").get("description").getAsString()).isEqualTo("Number of hello invocations");
        assertThat(app.getAsJsonObject("hello-count").get("unit").getAsString()).isEqualTo("none");
        assertThat(app.has("hello-time")).isTrue();
        assertThat(app.getAsJsonObject("hello-time").get("displayName").getAsString()).isEqualTo("Hello Time");
        assertThat(app.getAsJsonObject("hello-time").get("description").getAsString()).isEqualTo("Time of hello invocations");
        assertThat(app.getAsJsonObject("hello-time").get("unit").getAsString()).isEqualTo("milliseconds");
        assertThat(app.has("hello-freq")).isTrue();
        assertThat(app.getAsJsonObject("hello-freq").get("displayName").getAsString()).isEqualTo("Hello Freq");
        assertThat(app.getAsJsonObject("hello-freq").get("description").getAsString()).isEqualTo("Frequency of hello invocations");
        assertThat(app.getAsJsonObject("hello-freq").get("unit").getAsString()).isEqualTo("per_second");
    }

    @Test
    @RunAsClient
    @InSequence(4)
    public void jsonData() throws IOException {
        String response = Request.Get("http://localhost:8080/metrics")
                .addHeader("Accept", "application/json").execute().returnContent().asString();
        JsonObject json = new JsonParser().parse(response).getAsJsonObject();
        assertThat(json.has("application")).isTrue();
        JsonObject app = json.getAsJsonObject("application");
        assertThat(app.has("hello-count")).isTrue();
        assertThat(app.get("hello-count").getAsInt()).isEqualTo(11);
        assertThat(app.has("hello-time")).isTrue();
        assertThat(app.getAsJsonObject("hello-time").get("count").getAsInt()).isEqualTo(11);
        assertThat(app.getAsJsonObject("hello-time").get("min").getAsDouble()).isGreaterThanOrEqualTo(1.0)
                .isLessThanOrEqualTo(110.0); // delay is from 1 to 100 millis, 10 millis should be enough of a tolerance
        assertThat(app.getAsJsonObject("hello-time").get("max").getAsDouble()).isGreaterThanOrEqualTo(1.0)
                .isLessThanOrEqualTo(110.0); // delay is from 1 to 100 millis, 10 millis should be enough of a tolerance
        assertThat(app.has("hello-freq")).isTrue();
        assertThat(app.getAsJsonObject("hello-freq").get("count").getAsInt()).isEqualTo(11);
    }

    @Test
    @RunAsClient
    @InSequence(5)
    public void prometheusData() throws IOException {
        String response = Request.Get("http://localhost:8080/metrics").execute().returnContent().asString();
        assertThat(response).contains("application:hello_count 11.0");
        assertThat(prometheusMetricValue(response, "application:hello_time_min_seconds")).isGreaterThanOrEqualTo(0.001)
                .isLessThanOrEqualTo(0.110); // delay is from 1 to 100 millis, 10 millis should be enough of a tolerance
        assertThat(prometheusMetricValue(response, "application:hello_time_max_seconds")).isGreaterThanOrEqualTo(0.001)
                .isLessThanOrEqualTo(0.110); // delay is from 1 to 100 millis, 10 millis should be enough of a tolerance
        assertThat(response).contains("application:hello_freq_total 11.0");
    }

    private static double prometheusMetricValue(String prometheusResponse, String metricName) {
        String lineBeginning = metricName + " ";
        return Arrays.stream(prometheusResponse.split("\n"))
                .filter(line -> line.startsWith(lineBeginning))
                .map(line -> line.substring(lineBeginning.length()))
                .mapToDouble(Double::parseDouble)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Metric " + metricName + " not found in response"));
    }

    @Test
    @RunAsClient
    @InSequence(6)
    public void vendorMetrics() throws IOException {
        String response = Request.Get("http://localhost:8080/metrics")
                .addHeader("Accept", "application/json").execute().returnContent().asString();
        JsonObject json = new JsonParser().parse(response).getAsJsonObject();
        assertThat(json.has("vendor")).isTrue();
        JsonObject vendor = json.getAsJsonObject("vendor");
        assertThat(vendor.has("loadedModules")).isTrue();
        assertThat(vendor.has("mscLoadedModules")).isFalse();
        assertThat(vendor.has("test")).isFalse();
    }

    @Test
    @RunAsClient
    @InSequence(7)
    @Ignore("THORN-2050")
    public void metricRegistryInjection() throws IOException {
        String response = Request.Get("http://localhost:8080/summary?of=all-registries").execute().returnContent().asString();
        JsonObject json = new JsonParser().parse(response).getAsJsonObject();

        assertThat(json.has("base")).isTrue();
        JsonArray base = json.getAsJsonArray("base");
        assertThat(base.contains(new JsonPrimitive("memory.usedHeap"))).isTrue();
        assertThat(base.contains(new JsonPrimitive("memory.committedHeap"))).isTrue();
        assertThat(base.contains(new JsonPrimitive("memory.maxHeap"))).isTrue();
        assertThat(base.contains(new JsonPrimitive("jvm.uptime"))).isTrue();
        assertThat(base.contains(new JsonPrimitive("thread.count"))).isTrue();
        assertThat(base.contains(new JsonPrimitive("thread.daemon.count"))).isTrue();
        assertThat(base.contains(new JsonPrimitive("thread.max.count"))).isTrue();
        assertThat(base.contains(new JsonPrimitive("classloader.currentLoadedClass.count"))).isTrue();
        assertThat(base.contains(new JsonPrimitive("classloader.totalLoadedClass.count"))).isTrue();
        assertThat(base.contains(new JsonPrimitive("classloader.totalUnloadedClass.count"))).isTrue();
        assertThat(base.contains(new JsonPrimitive("cpu.availableProcessors"))).isTrue();

        assertThat(json.has("vendor")).isTrue();
        JsonArray vendor = json.getAsJsonArray("vendor");
        // don't want to hardcode all values, as they can change, so just pick one
        assertThat(vendor.contains(new JsonPrimitive("loadedModules"))).isTrue();

        assertThat(json.has("app")).isTrue();
        JsonArray app = json.getAsJsonArray("app");
        assertThat(app.size()).isEqualTo(3);
        assertThat(app.contains(new JsonPrimitive("hello-count"))).isTrue();
        assertThat(app.contains(new JsonPrimitive("hello-time"))).isTrue();
        assertThat(app.contains(new JsonPrimitive("hello-freq"))).isTrue();
    }

    @Test
    @RunAsClient
    @InSequence(8)
    @Ignore("THORN-2051")
    public void getAllRegisteredMetricsOfGivenType() throws IOException {
        String response = Request.Get("http://localhost:8080/summary?of=app-registry").execute().returnContent().asString();
        JsonObject json = new JsonParser().parse(response).getAsJsonObject();

        assertThat(json.has("app-counters")).isTrue();
        JsonArray appCounters = json.getAsJsonArray("app-counters");
        assertThat(appCounters.size()).isEqualTo(1);
        assertThat(appCounters.contains(new JsonPrimitive("hello-count"))).isTrue();

        assertThat(json.has("app-timers")).isTrue();
        JsonArray appTimers = json.getAsJsonArray("app-timers");
        assertThat(appTimers.size()).isEqualTo(1);
        assertThat(appTimers.contains(new JsonPrimitive("hello-time"))).isTrue();

        assertThat(json.has("app-meters")).isTrue();
        JsonArray appMeters = json.getAsJsonArray("app-meters");
        assertThat(appMeters.size()).isEqualTo(1);
        assertThat(appMeters.contains(new JsonPrimitive("hello-freq"))).isTrue();

        assertThat(json.has("app-gauges")).isTrue();
        JsonArray appGauges = json.getAsJsonArray("app-gauges");
        assertThat(appGauges.size()).isEqualTo(0);

        assertThat(json.has("app-histograms")).isTrue();
        JsonArray appHistograms = json.getAsJsonArray("app-histograms");
        assertThat(appHistograms.size()).isEqualTo(0);
    }
}
