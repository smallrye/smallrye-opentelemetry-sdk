package io.smallrye.opentelemetry.sdk.tck.tracing;

import static org.assertj.core.api.Assertions.assertThat;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.TracerProvider;

class OpenTelemetryTracingTest {

    //TODO Uncomment when tracer present
    //    @Test
    void testTracerProviderLookup() {
        TracerProvider tracerProvider = OpenTelemetry.getGlobalTracerProvider();
        assertThat(tracerProvider).isNotNull();
    }
}
