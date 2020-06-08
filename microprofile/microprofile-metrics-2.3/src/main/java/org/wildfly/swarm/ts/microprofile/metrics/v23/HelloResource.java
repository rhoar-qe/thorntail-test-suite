package org.wildfly.swarm.ts.microprofile.metrics.v23;

import java.util.Arrays;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetricID;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricType;
import org.eclipse.microprofile.metrics.Tag;
import org.eclipse.microprofile.metrics.annotation.Metric;
import org.eclipse.microprofile.metrics.annotation.SimplyTimed;

@Path("/")
public class HelloResource {

    @Inject
    private MetricRegistry metrics;

    private Counter counter;

    @Metric(name = "tags-holder")
    private Counter tagHolder;

    @PostConstruct
    private void setupMetrics() {
        counter = metrics.counter(Metadata.builder()
          .withName("counter")
          .withOptionalDisplayName(null)
          .withOptionalDescription(null)
          .withOptionalUnit(null)
          .withOptionalType(MetricType.fromClassName("org.eclipse.microprofile.metrics.Counter"))
          .build());
        Tag[] tags = new Tag[] {
                new Tag("tag1", "value1")
        };
        metrics.counter("tags-holder", tags);
    }

    @GET
    @Path("simple-timer")
    @SimplyTimed(name = "simple-timer", absolute = true, unit = "milliseconds")
    public String simplyTimed() throws InterruptedException {
        Thread.sleep(10);
        return "Hello from timed method";
    }

    @GET
    @Path("optional-metadata")
    public String optionalMetadata() {
        counter.inc();
        return "optional metadata";
    }

    @GET
    @Path("tags-as-array")
    public String getTagsAsArray() {
        MetricID metricID = metrics.getCounters((mID, metric) -> mID.getName().equals("tags-holder")).firstKey();
        return Arrays.stream(metricID.getTagsAsArray())
                .map(c -> c.getTagName() + ":" + c.getTagValue())
                .collect(Collectors.joining(","));
    }
}
