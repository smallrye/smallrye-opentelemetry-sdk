package io.smallrye.opentelemetry.sdk.metrics.impl;

import java.util.Objects;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import io.opentelemetry.common.Labels;
import io.opentelemetry.metrics.DoubleCounter;
import io.smallrye.opentelemetry.sdk.metrics.OpenTelemetryMeterRegistry;
import io.smallrye.opentelemetry.sdk.metrics.utils.LabelConverter;

public class DoubleCounterImpl implements DoubleCounter, DoubleCounter.BoundDoubleCounter {
    Meter.Id meterId;

    public DoubleCounterImpl(Meter.Id meterId) {
        this.meterId = meterId;
    }

    // DoubleCounter implementations
    @Override
    public void add(double increment, Labels labels) {
        if (increment < 0) {
            throw new IllegalArgumentException("DoubleCounter can only increase.");
        }

        counter(labels).increment(increment);
    }

    @Override
    public BoundDoubleCounter bind(Labels labels) {
        return new DoubleCounterImpl(this.meterId.withTags(LabelConverter.toTags(labels)));
    }

    // DoubleCounter.BoundDoubleCounter implementations
    @Override
    public void add(double increment) {
        add(increment, Labels.empty());
    }

    @Override
    public void unbind() {
        // No Op
    }

    // Internal methods
    private Counter counter(Labels labels) {
        return Counter.builder(meterId.getName())
                .description(meterId.getDescription())
                .baseUnit(meterId.getBaseUnit())
                .tags(LabelConverter.toTags(labels).and(meterId.getTags()))
                .register(OpenTelemetryMeterRegistry.INSTANCE);
    }

    public static final class Builder implements DoubleCounter.Builder {
        private final String name;
        private String description;
        private String baseUnit = "1";
        private Labels labels = Labels.empty();

        public Builder(String name) {
            this.name = name;
        }

        @Override
        public DoubleCounter.Builder setDescription(String description) {
            this.description = Objects.requireNonNull(description);
            return this;
        }

        @Override
        public DoubleCounter.Builder setUnit(String unit) {
            this.baseUnit = Objects.requireNonNull(unit);
            return this;
        }

        private Meter.Id constructMeterId() {
            return new Meter.Id(name, LabelConverter.toTags(labels), baseUnit, description, Meter.Type.COUNTER);
        }

        @Override
        public DoubleCounter build() {
            return new DoubleCounterImpl(constructMeterId());
        }
    }
}
