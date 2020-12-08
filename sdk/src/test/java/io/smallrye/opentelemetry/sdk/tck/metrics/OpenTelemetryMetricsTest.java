package io.smallrye.opentelemetry.sdk.tck.metrics;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.metrics.MeterProvider;

class OpenTelemetryMetricsTest {

    @Test
    void testMeterProviderLookup() {
        MeterProvider meterProvider = OpenTelemetry.getGlobalMeterProvider();
        assertThat(meterProvider)
                .isNotNull();
    }

    @Test
    void testMeterRetrievalByNameFromProvider() {
        MeterProvider meterProvider = OpenTelemetry.getGlobalMeterProvider();
        assertThat(meterProvider)
                .isNotNull();

        Meter meter = meterProvider.get("io.smallrye.opentelemetry");
        assertThat(meter)
                .isNotNull()
                .isNotEqualTo(Meter.getDefault());
    }

    @Test
    void testMeterRetrievalByNameAndVersionFromProvider() {
        MeterProvider meterProvider = OpenTelemetry.getGlobalMeterProvider();
        assertThat(meterProvider)
                .isNotNull();

        Meter meter = meterProvider.get("io.smallrye.opentelemetry", "0.1.0");
        assertThat(meter)
                .isNotNull()
                .isNotEqualTo(Meter.getDefault());
    }
}
