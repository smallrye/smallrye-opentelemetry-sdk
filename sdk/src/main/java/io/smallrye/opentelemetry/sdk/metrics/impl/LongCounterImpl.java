package io.smallrye.opentelemetry.sdk.metrics.impl;

import java.util.Objects;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import io.opentelemetry.api.common.Labels;
import io.opentelemetry.api.metrics.LongCounter;
import io.smallrye.opentelemetry.sdk.metrics.OpenTelemetryMeterRegistry;
import io.smallrye.opentelemetry.sdk.metrics.utils.LabelConverter;

public class LongCounterImpl implements LongCounter, LongCounter.BoundLongCounter {
    Meter.Id meterId;

    public LongCounterImpl(Meter.Id meterId) {
        this.meterId = meterId;
    }

    // LongCounter implementations
    @Override
    public void add(long increment, Labels labels) {
        if (increment < 0) {
            throw new IllegalArgumentException("DoubleCounter can only increase.");
        }

        counter(labels).increment(increment);
    }

    @Override
    public BoundLongCounter bind(Labels labels) {
        return new LongCounterImpl(this.meterId.withTags(LabelConverter.toTags(labels)));
    }

    // LongCounter.BoundLongCounter implementations
    @Override
    public void add(long increment) {
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

    public static final class Builder implements LongCounter.Builder {
        private final String name;
        private String description;
        private String baseUnit = "1";
        private Labels labels = Labels.empty();

        public Builder(String name) {
            this.name = name;
        }

        @Override
        public LongCounter.Builder setDescription(String description) {
            this.description = Objects.requireNonNull(description);
            return this;
        }

        @Override
        public LongCounter.Builder setUnit(String unit) {
            this.baseUnit = Objects.requireNonNull(unit);
            return this;
        }

        private Meter.Id constructMeterId() {
            return new Meter.Id(name, LabelConverter.toTags(labels), baseUnit, description, Meter.Type.COUNTER);
        }

        @Override
        public LongCounter build() {
            return new LongCounterImpl(constructMeterId());
        }
    }
}
