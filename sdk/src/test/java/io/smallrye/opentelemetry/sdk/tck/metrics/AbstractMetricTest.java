package io.smallrye.opentelemetry.sdk.tck.metrics;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.smallrye.opentelemetry.sdk.metrics.OpenTelemetryMeterRegistry;

public abstract class AbstractMetricTest {
    protected static SimpleMeterRegistry collector;

    @BeforeAll
    static void setupMetrics() {
        collector = new SimpleMeterRegistry();
        OpenTelemetryMeterRegistry.INSTANCE.add(collector);
        Metrics.addRegistry(OpenTelemetryMeterRegistry.INSTANCE);
    }

    @BeforeEach
    void clearMetrics() {
        collector.clear();
    }
}
