package io.smallrye.opentelemetry.sdk.tck.metrics;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Statistic;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.opentelemetry.OpenTelemetry;
import io.opentelemetry.common.Labels;
import io.opentelemetry.metrics.DoubleCounter;
import io.smallrye.opentelemetry.sdk.metrics.OpenTelemetryMeterRegistry;

class DoubleCounterTest {
    private static SimpleMeterRegistry collector;

    @BeforeAll
    static void setupMetrics() {
        collector = new SimpleMeterRegistry();
        OpenTelemetryMeterRegistry.INSTANCE.add(collector);
        Metrics.addRegistry(OpenTelemetryMeterRegistry.INSTANCE);
    }

    @BeforeEach
    void clearMetrics() {
        collector.clear();
    }

    @Test
    void testWithNoLabels() {
        final String counterName = "my-counter-no-labels";
        final String counterDescription = "Description of my-counter-no-labels";

        DoubleCounter doubleCounter = OpenTelemetry.getMeter("io.smallrye.opentelemetry.sdk")
                .doubleCounterBuilder(counterName)
                .setDescription(counterDescription)
                .build();

        doubleCounter.add(2, Labels.empty());

        List<Meter> meters = collector.getMeters();
        assertThat(meters).isNotNull();
        assertThat(meters.size()).isEqualTo(1);

        Meter meter = meters.get(0);
        assertThat(meter).isNotNull();
        assertThat(meter.getId()).isNotNull();
        assertThat(meter.getId().getName()).isEqualTo(counterName);
        assertThat(meter.getId().getDescription()).isEqualTo(counterDescription);
        assertThat(meter.getId().getBaseUnit()).isEqualTo("1");
        assertThat(meter.getId().getTags()).isEmpty();

        Iterator<Measurement> iterator = meter.measure().iterator();
        assertThat(iterator.hasNext()).isTrue();

        Measurement measure = iterator.next();
        assertThat(measure.getStatistic()).isEqualTo(Statistic.COUNT);
        assertThat(measure.getValue()).isEqualTo(2.0);

        assertThat(iterator.hasNext()).isFalse();
    }

    @Test
    void testWithLabelsOnConstruct() {
        final String counterName = "my-counter-labels-construct";
        final String counterDescription = "Description of my-counter-labels-construct";
        final String labelKey = "myKey";
        final String labelValue = "myValue";

        DoubleCounter doubleCounter = OpenTelemetry.getMeter("io.smallrye.opentelemetry.sdk")
                .doubleCounterBuilder(counterName)
                .setDescription(counterDescription)
                .setConstantLabels(Labels.of(labelKey, labelValue))
                .build();

        doubleCounter.add(4, Labels.empty());

        List<Meter> meters = collector.getMeters();
        assertThat(meters).isNotNull();
        assertThat(meters.size()).isEqualTo(1);

        Meter meter = meters.get(0);
        assertThat(meter).isNotNull();
        assertThat(meter.getId()).isNotNull();
        assertThat(meter.getId().getName()).isEqualTo(counterName);
        assertThat(meter.getId().getDescription()).isEqualTo(counterDescription);
        assertThat(meter.getId().getBaseUnit()).isEqualTo("1");
        assertThat(meter.getId().getTags()).isNotEmpty();
        assertThat(meter.getId().getTags().size()).isEqualTo(1);
        assertThat(meter.getId().getTags().get(0)).isNotNull();
        assertThat(meter.getId().getTags().get(0).getKey()).isEqualTo(labelKey);
        assertThat(meter.getId().getTags().get(0).getValue()).isEqualTo(labelValue);

        Iterator<Measurement> iterator = meter.measure().iterator();
        assertThat(iterator.hasNext()).isTrue();

        Measurement measure = iterator.next();
        assertThat(measure.getStatistic()).isEqualTo(Statistic.COUNT);
        assertThat(measure.getValue()).isEqualTo(4.0);

        assertThat(iterator.hasNext()).isFalse();
    }

    @Test
    void testWithLabelsOnIncrement() {
        final String counterName = "my-counter-labels-increment";
        final String counterDescription = "Description of my-counter-labels-increment";
        final String labelKey = "myKey";
        final String labelValue = "myValue";

        DoubleCounter doubleCounter = OpenTelemetry.getMeter("io.smallrye.opentelemetry.sdk")
                .doubleCounterBuilder(counterName)
                .setDescription(counterDescription)
                .build();

        doubleCounter.add(6, Labels.of(labelKey, labelValue));

        List<Meter> meters = collector.getMeters();
        assertThat(meters).isNotNull();
        assertThat(meters.size()).isEqualTo(1);

        Meter meter = meters.get(0);
        assertThat(meter).isNotNull();
        assertThat(meter.getId()).isNotNull();
        assertThat(meter.getId().getName()).isEqualTo(counterName);
        assertThat(meter.getId().getDescription()).isEqualTo(counterDescription);
        assertThat(meter.getId().getBaseUnit()).isEqualTo("1");
        assertThat(meter.getId().getTags()).isNotEmpty();
        assertThat(meter.getId().getTags().size()).isEqualTo(1);
        assertThat(meter.getId().getTags().get(0)).isNotNull();
        assertThat(meter.getId().getTags().get(0).getKey()).isEqualTo(labelKey);
        assertThat(meter.getId().getTags().get(0).getValue()).isEqualTo(labelValue);

        Iterator<Measurement> iterator = meter.measure().iterator();
        assertThat(iterator.hasNext()).isTrue();

        Measurement measure = iterator.next();
        assertThat(measure.getStatistic()).isEqualTo(Statistic.COUNT);
        assertThat(measure.getValue()).isEqualTo(6.0);

        assertThat(iterator.hasNext()).isFalse();
    }

    @Test
    void testWithLabelsOnConstructAndIncrement() {
        final String counterName = "my-counter-labels-construct-increment";
        final String counterDescription = "Description of my-counter-labels-construct-increment";
        final String labelKey1 = "myKey1";
        final String labelValue1 = "myValue1";
        final String labelKey2 = "myKey2";
        final String labelValue2 = "myValue2";

        DoubleCounter doubleCounter = OpenTelemetry.getMeter("io.smallrye.opentelemetry.sdk")
                .doubleCounterBuilder(counterName)
                .setDescription(counterDescription)
                .setConstantLabels(Labels.of(labelKey1, labelValue1))
                .build();

        doubleCounter.add(4.5, Labels.of(labelKey2, labelValue2));

        List<Meter> meters = collector.getMeters();
        assertThat(meters).isNotNull();
        assertThat(meters.size()).isEqualTo(1);

        Meter meter = meters.get(0);
        assertThat(meter).isNotNull();
        assertThat(meter.getId()).isNotNull();
        assertThat(meter.getId().getName()).isEqualTo(counterName);
        assertThat(meter.getId().getDescription()).isEqualTo(counterDescription);
        assertThat(meter.getId().getBaseUnit()).isEqualTo("1");
        assertThat(meter.getId().getTags()).isNotEmpty();
        assertThat(meter.getId().getTags().size()).isEqualTo(2);
        assertThat(meter.getId().getTags().get(0)).isNotNull();
        assertThat(meter.getId().getTags().get(0).getKey()).isEqualTo(labelKey1);
        assertThat(meter.getId().getTags().get(0).getValue()).isEqualTo(labelValue1);
        assertThat(meter.getId().getTags().get(1)).isNotNull();
        assertThat(meter.getId().getTags().get(1).getKey()).isEqualTo(labelKey2);
        assertThat(meter.getId().getTags().get(1).getValue()).isEqualTo(labelValue2);

        Iterator<Measurement> iterator = meter.measure().iterator();
        assertThat(iterator.hasNext()).isTrue();

        Measurement measure = iterator.next();
        assertThat(measure.getStatistic()).isEqualTo(Statistic.COUNT);
        assertThat(measure.getValue()).isEqualTo(4.5);

        assertThat(iterator.hasNext()).isFalse();
    }

    @Test
    void testWithMultiIncrementNoLabels() {
        final String counterName = "my-counter-multi-increment-no-labels";
        final String counterDescription = "Description of my-counter-multi-increment-no-labels";

        DoubleCounter doubleCounter = OpenTelemetry.getMeter("io.smallrye.opentelemetry.sdk")
                .doubleCounterBuilder(counterName)
                .setDescription(counterDescription)
                .build();

        doubleCounter.add(1.5, Labels.empty());

        List<Meter> meters = collector.getMeters();
        assertThat(meters).isNotNull();
        assertThat(meters.size()).isEqualTo(1);

        Meter meter = meters.get(0);
        assertThat(meter).isNotNull();
        assertThat(meter.getId()).isNotNull();
        assertThat(meter.getId().getName()).isEqualTo(counterName);
        assertThat(meter.getId().getDescription()).isEqualTo(counterDescription);
        assertThat(meter.getId().getBaseUnit()).isEqualTo("1");
        assertThat(meter.getId().getTags()).isEmpty();

        Iterator<Measurement> iterator = meter.measure().iterator();
        assertThat(iterator.hasNext()).isTrue();

        Measurement measure = iterator.next();
        assertThat(measure.getStatistic()).isEqualTo(Statistic.COUNT);
        assertThat(measure.getValue()).isEqualTo(1.5);

        assertThat(iterator.hasNext()).isFalse();

        doubleCounter.add(4.2, Labels.empty());

        meters = collector.getMeters();
        assertThat(meters).isNotNull();
        assertThat(meters.size()).isEqualTo(1);

        meter = meters.get(0);
        assertThat(meter).isNotNull();
        assertThat(meter.getId()).isNotNull();
        assertThat(meter.getId().getName()).isEqualTo(counterName);
        assertThat(meter.getId().getDescription()).isEqualTo(counterDescription);
        assertThat(meter.getId().getBaseUnit()).isEqualTo("1");
        assertThat(meter.getId().getTags()).isEmpty();

        iterator = meter.measure().iterator();
        assertThat(iterator.hasNext()).isTrue();

        measure = iterator.next();
        assertThat(measure.getStatistic()).isEqualTo(Statistic.COUNT);
        assertThat(measure.getValue()).isEqualTo(5.7);

        assertThat(iterator.hasNext()).isFalse();
    }

    @Test
    void testBindWithNoLabels() {
        final String counterName = "my-counter-binding-no-labels";
        final String counterDescription = "Description of my-counter-binding-no-labels";
        final String labelKey = "myKey";
        final String labelValue = "myValue";

        DoubleCounter doubleCounter = OpenTelemetry.getMeter("io.smallrye.opentelemetry.sdk")
                .doubleCounterBuilder(counterName)
                .setDescription(counterDescription)
                .build();

        DoubleCounter.BoundDoubleCounter boundDoubleCounter = doubleCounter.bind(Labels.of(labelKey, labelValue));
        boundDoubleCounter.add(3.1);

        List<Meter> meters = collector.getMeters();
        assertThat(meters).isNotNull();
        assertThat(meters.size()).isEqualTo(1);

        Meter meter = meters.get(0);
        assertThat(meter).isNotNull();
        assertThat(meter.getId()).isNotNull();
        assertThat(meter.getId().getName()).isEqualTo(counterName);
        assertThat(meter.getId().getDescription()).isEqualTo(counterDescription);
        assertThat(meter.getId().getBaseUnit()).isEqualTo("1");
        assertThat(meter.getId().getTags()).isNotEmpty();
        assertThat(meter.getId().getTags().size()).isEqualTo(1);
        assertThat(meter.getId().getTags().get(0)).isNotNull();
        assertThat(meter.getId().getTags().get(0).getKey()).isEqualTo(labelKey);
        assertThat(meter.getId().getTags().get(0).getValue()).isEqualTo(labelValue);

        Iterator<Measurement> iterator = meter.measure().iterator();
        assertThat(iterator.hasNext()).isTrue();

        Measurement measure = iterator.next();
        assertThat(measure.getStatistic()).isEqualTo(Statistic.COUNT);
        assertThat(measure.getValue()).isEqualTo(3.1);

        assertThat(iterator.hasNext()).isFalse();
    }
}
