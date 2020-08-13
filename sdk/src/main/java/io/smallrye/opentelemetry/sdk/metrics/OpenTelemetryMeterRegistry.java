package io.smallrye.opentelemetry.sdk.metrics;

import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.opentelemetry.metrics.BatchRecorder;
import io.opentelemetry.metrics.DoubleCounter;
import io.opentelemetry.metrics.DoubleSumObserver;
import io.opentelemetry.metrics.DoubleUpDownCounter;
import io.opentelemetry.metrics.DoubleUpDownSumObserver;
import io.opentelemetry.metrics.DoubleValueObserver;
import io.opentelemetry.metrics.DoubleValueRecorder;
import io.opentelemetry.metrics.LongCounter;
import io.opentelemetry.metrics.LongSumObserver;
import io.opentelemetry.metrics.LongUpDownCounter;
import io.opentelemetry.metrics.LongUpDownSumObserver;
import io.opentelemetry.metrics.LongValueObserver;
import io.opentelemetry.metrics.LongValueRecorder;
import io.opentelemetry.metrics.Meter;
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
