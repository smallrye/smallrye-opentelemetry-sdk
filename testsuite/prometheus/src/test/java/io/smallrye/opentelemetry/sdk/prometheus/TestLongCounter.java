package io.smallrye.opentelemetry.sdk.prometheus;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.opentelemetry.OpenTelemetry;
import io.opentelemetry.common.Labels;
import io.opentelemetry.metrics.LongCounter;

class TestLongCounter extends AbstractTestBase {

    @Test
    void testPrometheusOutputWithoutLabels() {
        LongCounter longCounter = OpenTelemetry.getMeter("nonsense-value")
                .longCounterBuilder("longCounter")
                .setDescription("This is my long counter")
                .build();

        longCounter.add(2, Labels.empty());

        LongCounter anotherCounter = OpenTelemetry.getMeter("nonsense-value")
                .longCounterBuilder("anotherLongCounter")
                .setDescription("This is another counter for longs")
                .build();

        anotherCounter.add(6, Labels.empty());

        // Verify Prometheus
        String output = prometheusMeterRegistry.scrape();

        assertThat(output)
                .contains("# HELP longCounter_1_total This is my long counter")
                .contains("# TYPE longCounter_1_total counter")
                .contains("longCounter_1_total 2.0")
                .contains("# HELP anotherLongCounter_1_total This is another counter for longs")
                .contains("# TYPE anotherLongCounter_1_total counter")
                .contains("anotherLongCounter_1_total 6.0");
    }

    @Test
    void testPrometheusOutputWithLabels() {
        LongCounter longCounter = OpenTelemetry.getMeter("nonsense-value")
                .longCounterBuilder("longCounterLabels")
                .setDescription("This is my long counter with labels")
                .build();

        longCounter.add(4, Labels.of("myKey", "aValue"));

        LongCounter anotherCounter = OpenTelemetry.getMeter("nonsense-value")
                .longCounterBuilder("anotherLongCounterLabels")
                .setDescription("This is another counter for longs with labels")
                .build();

        anotherCounter.add(6, Labels.of("someKey", "someValue"));

        longCounter.add(3, Labels.of("myKey", "aValue"));
        anotherCounter.add(8, Labels.of("someKey", "someValue"));

        // Verify Prometheus
        String output = prometheusMeterRegistry.scrape();

        assertThat(output)
                .contains("# HELP longCounterLabels_1_total This is my long counter with labels")
                .contains("# TYPE longCounterLabels_1_total counter")
                .contains("longCounterLabels_1_total{myKey=\"aValue\",} 7.0")
                .contains("# HELP anotherLongCounterLabels_1_total This is another counter for longs with labels")
                .contains("# TYPE anotherLongCounterLabels_1_total counter")
                .contains("anotherLongCounterLabels_1_total{someKey=\"someValue\",} 14.0");
    }
}
