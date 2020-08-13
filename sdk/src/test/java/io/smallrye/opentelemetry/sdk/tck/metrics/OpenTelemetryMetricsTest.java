package io.smallrye.opentelemetry.sdk.tck.metrics;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.opentelemetry.OpenTelemetry;
import io.opentelemetry.metrics.DefaultMeter;
import io.opentelemetry.metrics.DefaultMeterProvider;
import io.opentelemetry.metrics.Meter;
import io.opentelemetry.metrics.MeterProvider;

public class OpenTelemetryMetricsTest {

    @Test
    void testMeterProviderLookup() {
        MeterProvider meterProvider = OpenTelemetry.getMeterProvider();
        assertThat(meterProvider).isNotNull();
        assertThat(meterProvider).isNotInstanceOf(DefaultMeterProvider.class);
    }

    @Test
    void testMeterRetrievalByNameFromProvider() {
        MeterProvider meterProvider = OpenTelemetry.getMeterProvider();
        assertThat(meterProvider).isNotNull();
        assertThat(meterProvider).isNotInstanceOf(DefaultMeterProvider.class);

        Meter meter = meterProvider.get("io.smallrye.opentelemetry");
        assertThat(meter).isNotNull();
        assertThat(meter).isNotInstanceOf(DefaultMeter.class);
    }

    @Test
    void testMeterRetrievalByNameAndVersionFromProvider() {
        MeterProvider meterProvider = OpenTelemetry.getMeterProvider();
        assertThat(meterProvider).isNotNull();
        assertThat(meterProvider).isNotInstanceOf(DefaultMeterProvider.class);

        Meter meter = meterProvider.get("io.smallrye.opentelemetry", "0.1.0");
        assertThat(meter).isNotNull();
        assertThat(meter).isNotInstanceOf(DefaultMeter.class);
    }
}
