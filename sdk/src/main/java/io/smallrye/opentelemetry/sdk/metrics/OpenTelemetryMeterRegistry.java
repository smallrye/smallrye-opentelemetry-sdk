package io.smallrye.opentelemetry.sdk.metrics;

import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.opentelemetry.api.metrics.BatchRecorder;
import io.opentelemetry.api.metrics.DoubleCounter;
import io.opentelemetry.api.metrics.DoubleSumObserver;
import io.opentelemetry.api.metrics.DoubleUpDownCounter;
import io.opentelemetry.api.metrics.DoubleUpDownSumObserver;
import io.opentelemetry.api.metrics.DoubleValueObserver;
import io.opentelemetry.api.metrics.DoubleValueRecorder;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.LongSumObserver;
import io.opentelemetry.api.metrics.LongUpDownCounter;
import io.opentelemetry.api.metrics.LongUpDownSumObserver;
import io.opentelemetry.api.metrics.LongValueObserver;
import io.opentelemetry.api.metrics.LongValueRecorder;
import io.opentelemetry.api.metrics.Meter;
import io.smallrye.opentelemetry.sdk.metrics.impl.DoubleCounterImpl;
import io.smallrye.opentelemetry.sdk.metrics.impl.LongCounterImpl;

public class OpenTelemetryMeterRegistry extends CompositeMeterRegistry implements Meter {

    public static final OpenTelemetryMeterRegistry INSTANCE = new OpenTelemetryMeterRegistry();

    @Override
    public DoubleCounter.Builder doubleCounterBuilder(String name) {
        return new DoubleCounterImpl.Builder(name);
    }

    @Override
    public LongCounter.Builder longCounterBuilder(String name) {
        return new LongCounterImpl.Builder(name);
    }

    @Override
    public DoubleUpDownCounter.Builder doubleUpDownCounterBuilder(String name) {
        return null;
    }

    @Override
    public LongUpDownCounter.Builder longUpDownCounterBuilder(String name) {
        return null;
    }

    @Override
    public DoubleValueRecorder.Builder doubleValueRecorderBuilder(String name) {
        return null;
    }

    @Override
    public LongValueRecorder.Builder longValueRecorderBuilder(String name) {
        return null;
    }

    @Override
    public DoubleSumObserver.Builder doubleSumObserverBuilder(String name) {
        return null;
    }

    @Override
    public LongSumObserver.Builder longSumObserverBuilder(String name) {
        return null;
    }

    @Override
    public DoubleUpDownSumObserver.Builder doubleUpDownSumObserverBuilder(String name) {
        return null;
    }

    @Override
    public LongUpDownSumObserver.Builder longUpDownSumObserverBuilder(String name) {
        return null;
    }

    @Override
    public DoubleValueObserver.Builder doubleValueObserverBuilder(String name) {
        return null;
    }

    @Override
    public LongValueObserver.Builder longValueObserverBuilder(String name) {
        return null;
    }

    @Override
    public BatchRecorder newBatchRecorder(String... keyValuePairs) {
        return null;
    }
}
