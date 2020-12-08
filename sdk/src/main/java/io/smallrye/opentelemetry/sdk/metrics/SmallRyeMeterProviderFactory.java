package io.smallrye.opentelemetry.sdk.metrics;

import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.metrics.MeterProvider;
import io.opentelemetry.spi.metrics.MeterProviderFactory;

public class SmallRyeMeterProviderFactory implements MeterProvider, MeterProviderFactory {

    @Override
    public Meter get(String instrumentationName) {
        return get(instrumentationName, null);
    }

    @Override
    public Meter get(String instrumentationName, String instrumentationVersion) {
        //TODO Should we set common tag on the registry of instrumentation name and version?
        return OpenTelemetryMeterRegistry.INSTANCE;
    }

    @Override
    public MeterProvider create() {
        return this;
    }
}
