package org.wildfly.swarm.ts.microprofile.metrics.v10;

import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.annotation.RegistryType;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

@ApplicationScoped
public class MetricsSummary {
    @Inject
    @RegistryType(type = MetricRegistry.Type.BASE)
    private MetricRegistry baseMetrics;

    @Inject
    @RegistryType(type = MetricRegistry.Type.VENDOR)
    private MetricRegistry vendorMetrics;

    @Inject
    @RegistryType(type = MetricRegistry.Type.APPLICATION)
    private MetricRegistry appMetrics;

    public JsonObject summarizeAllRegistries() {
        JsonArrayBuilder base = Json.createArrayBuilder();
        baseMetrics.getMetrics().keySet().forEach(base::add);

        JsonArrayBuilder vendor = Json.createArrayBuilder();
        vendorMetrics.getMetrics().keySet().forEach(vendor::add);

        JsonArrayBuilder app = Json.createArrayBuilder();
        appMetrics.getMetrics().keySet().forEach(app::add);

        return Json.createObjectBuilder()
                .add("base", base)
                .add("vendor", vendor)
                .add("app", app)
                .build();
    }

    public JsonObject summarizeAppRegistry() {
        JsonArrayBuilder appCounters = Json.createArrayBuilder();
        appMetrics.getCounters().keySet().forEach(appCounters::add);

        JsonArrayBuilder appTimers = Json.createArrayBuilder();
        appMetrics.getTimers().keySet().forEach(appTimers::add);

        JsonArrayBuilder appMeters = Json.createArrayBuilder();
        appMetrics.getMeters().keySet().forEach(appMeters::add);

        JsonArrayBuilder appGauges = Json.createArrayBuilder();
        appMetrics.getGauges().keySet().forEach(appGauges::add);

        JsonArrayBuilder appHistograms = Json.createArrayBuilder();
        appMetrics.getHistograms().keySet().forEach(appHistograms::add);

        return Json.createObjectBuilder()
                .add("app-counters", appCounters)
                .add("app-timers", appTimers)
                .add("app-meters", appMeters)
                .add("app-gauges", appGauges)
                .add("app-histograms", appHistograms)
                .build();
    }
}
