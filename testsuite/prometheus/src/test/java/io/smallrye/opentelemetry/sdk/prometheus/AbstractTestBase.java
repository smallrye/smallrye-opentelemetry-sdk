package io.smallrye.opentelemetry.sdk.prometheus;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.smallrye.opentelemetry.sdk.metrics.OpenTelemetryMeterRegistry;

public abstract class AbstractTestBase {
    protected static PrometheusMeterRegistry prometheusMeterRegistry;

    @BeforeAll
    static void setupRegistry() {
        prometheusMeterRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        OpenTelemetryMeterRegistry.INSTANCE.add(prometheusMeterRegistry);
        Metrics.addRegistry(OpenTelemetryMeterRegistry.INSTANCE);
    }

    @BeforeEach
    void clearRegistry() {
        prometheusMeterRegistry.clear();
    }
}
