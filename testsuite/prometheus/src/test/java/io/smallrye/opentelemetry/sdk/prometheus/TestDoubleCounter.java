package io.smallrye.opentelemetry.sdk.prometheus;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.opentelemetry.OpenTelemetry;
import io.opentelemetry.common.Labels;
import io.opentelemetry.metrics.DoubleCounter;

class TestDoubleCounter extends AbstractTestBase {

    @Test
    void testPrometheusOutputWithoutLabels() {
        DoubleCounter doubleCounter = OpenTelemetry.getMeter("nonsense-value")
                .doubleCounterBuilder("doubleCounter")
                .setDescription("This is my first counter")
                .build();

        doubleCounter.add(2, Labels.empty());

        DoubleCounter anotherCounter = OpenTelemetry.getMeter("nonsense-value")
                .doubleCounterBuilder("anotherDoubleCounter")
                .setDescription("This is another counter")
                .build();

        anotherCounter.add(6.3, Labels.empty());

        // Verify Prometheus
        String output = prometheusMeterRegistry.scrape();

        assertThat(output)
                .contains("# HELP doubleCounter_1_total This is my first counter")
                .contains("# TYPE doubleCounter_1_total counter")
                .contains("doubleCounter_1_total 2.0")
                .contains("# HELP anotherDoubleCounter_1_total This is another counter")
                .contains("# TYPE anotherDoubleCounter_1_total counter")
                .contains("anotherDoubleCounter_1_total 6.3");
    }

    @Test
    void testPrometheusOutputWithLabels() {
        DoubleCounter doubleCounter = OpenTelemetry.getMeter("nonsense-value")
                .doubleCounterBuilder("doubleCounterLabels")
                .setDescription("This is my first counter with Labels")
                .build();

        doubleCounter.add(4, Labels.of("myKey", "aValue"));

        DoubleCounter anotherCounter = OpenTelemetry.getMeter("nonsense-value")
                .doubleCounterBuilder("anotherDoubleCounterLabels")
                .setDescription("This is another counter with Labels")
                .build();

        anotherCounter.add(6.3, Labels.of("someKey", "someValue"));

        doubleCounter.add(3.2, Labels.of("myKey", "aValue"));
        anotherCounter.add(0.4, Labels.of("someKey", "someValue"));

        // Verify Prometheus
        String output = prometheusMeterRegistry.scrape();

        assertThat(output)
                .contains("# HELP doubleCounterLabels_1_total This is my first counter with Labels")
                .contains("# TYPE doubleCounterLabels_1_total counter")
                .contains("doubleCounterLabels_1_total{myKey=\"aValue\",} 7.2")
                .contains("# HELP anotherDoubleCounterLabels_1_total This is another counter with Labels")
                .contains("# TYPE anotherDoubleCounterLabels_1_total counter")
                .contains("anotherDoubleCounterLabels_1_total{someKey=\"someValue\",} 6.7");
    }
}
