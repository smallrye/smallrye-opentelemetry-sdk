package io.smallrye.opentelemetry.sdk.tck.tracing;

import static org.assertj.core.api.Assertions.assertThat;

import io.opentelemetry.OpenTelemetry;
import io.opentelemetry.trace.DefaultTracerProvider;
import io.opentelemetry.trace.TracerProvider;

public class OpenTelemetryTracingTest {

    //TODO Uncomment when tracer present
    //    @Test
    void testTracerProviderLookup() {
        TracerProvider tracerProvider = OpenTelemetry.getTracerProvider();
        assertThat(tracerProvider).isNotNull();
        assertThat(tracerProvider).isNotInstanceOf(DefaultTracerProvider.class);
    }
}
