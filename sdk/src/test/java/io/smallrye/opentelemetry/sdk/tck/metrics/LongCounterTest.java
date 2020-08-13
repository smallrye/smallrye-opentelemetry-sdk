package io.smallrye.opentelemetry.sdk.tck.metrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Statistic;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.opentelemetry.OpenTelemetry;
import io.opentelemetry.common.Labels;
import io.opentelemetry.metrics.LongCounter;
import io.smallrye.opentelemetry.sdk.metrics.OpenTelemetryMeterRegistry;

class LongCounterTest {
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
        final String counterName = "long-counter-no-labels";
        final String counterDescription = "Description of long-counter-no-labels";

        LongCounter longCounter = OpenTelemetry.getMeter("io.smallrye.opentelemetry.sdk")
                .longCounterBuilder(counterName)
                .setDescription(counterDescription)
                .build();

        longCounter.add(6, Labels.empty());

        List<Meter> meters = collector.getMeters();
        assertThat(meters).isNotNull();
        assertThat(meters.size()).isEqualTo(1);

        verifyMeter(meters.get(0), counterName, counterDescription, "1", 0, null, null, 6.0);
    }

    @Test
    void testWithUnit() {
        final String counterName = "long-counter-unit";
        final String counterDescription = "Description of long-counter-unit";

        LongCounter longCounter = OpenTelemetry.getMeter("io.smallrye.opentelemetry.sdk")
                .longCounterBuilder(counterName)
                .setDescription(counterDescription)
                .setUnit("2")
                .build();

        longCounter.add(23, Labels.empty());

        List<Meter> meters = collector.getMeters();
        assertThat(meters).isNotNull();
        assertThat(meters.size()).isEqualTo(1);

        verifyMeter(meters.get(0), counterName, counterDescription, "2", 0, null, null, 23.0);
    }

    @Test
    void testIncrementFail() {
        final String counterName = "long-counter-fail";
        final String counterDescription = "Description of long-counter-fail";

        LongCounter longCounter = OpenTelemetry.getMeter("io.smallrye.opentelemetry.sdk")
                .longCounterBuilder(counterName)
                .setDescription(counterDescription)
                .build();

        Labels labels = Labels.empty();
        assertThrows(IllegalArgumentException.class, () -> longCounter.add(-43, labels));

        List<Meter> meters = collector.getMeters();
        assertThat(meters).isNotNull();
        assertThat(meters.size()).isZero();
    }

    @Test
    void testWithLabelsOnConstruct() {
        final String counterName = "long-counter-labels-construct";
        final String counterDescription = "Description of long-counter-labels-construct";
        final String labelKey = "myKey";
        final String labelValue = "myValue";

        LongCounter longCounter = OpenTelemetry.getMeter("io.smallrye.opentelemetry.sdk")
                .longCounterBuilder(counterName)
                .setDescription(counterDescription)
                .setConstantLabels(Labels.of(labelKey, labelValue))
                .build();

        longCounter.add(4, Labels.empty());

        List<Meter> meters = collector.getMeters();
        assertThat(meters).isNotNull();
        assertThat(meters.size()).isEqualTo(1);

        verifyMeter(meters.get(0), counterName, counterDescription, "1", 1, labelKey, labelValue, 4.0);
    }

    @Test
    void testWithLabelsOnIncrement() {
        final String counterName = "long-counter-labels-increment";
        final String counterDescription = "Description of long-counter-labels-increment";
        final String labelKey = "myKey";
        final String labelValue = "myValue";

        LongCounter longCounter = OpenTelemetry.getMeter("io.smallrye.opentelemetry.sdk")
                .longCounterBuilder(counterName)
                .setDescription(counterDescription)
                .build();

        longCounter.add(6, Labels.of(labelKey, labelValue));

        List<Meter> meters = collector.getMeters();
        assertThat(meters).isNotNull();
        assertThat(meters.size()).isEqualTo(1);

        verifyMeter(meters.get(0), counterName, counterDescription, "1", 1, labelKey, labelValue, 6.0);
    }

    @Test
    void testWithLabelsOnConstructAndIncrement() {
        final String counterName = "long-counter-labels-construct-increment";
        final String counterDescription = "Description of long-counter-labels-construct-increment";
        final String labelKey1 = "myKey1";
        final String labelValue1 = "myValue1";
        final String labelKey2 = "myKey2";
        final String labelValue2 = "myValue2";

        LongCounter longCounter = OpenTelemetry.getMeter("io.smallrye.opentelemetry.sdk")
                .longCounterBuilder(counterName)
                .setDescription(counterDescription)
                .setConstantLabels(Labels.of(labelKey1, labelValue1))
                .build();

        longCounter.add(45, Labels.of(labelKey2, labelValue2));

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
        assertThat(measure.getValue()).isEqualTo(45.0);

        assertThat(iterator.hasNext()).isFalse();
    }

    @Test
    void testMultiIncrementWithNoLabels() {
        final String counterName = "long-counter-multi-increment-no-labels";
        final String counterDescription = "Description of long-counter-multi-increment-no-labels";

        LongCounter longCounter = OpenTelemetry.getMeter("io.smallrye.opentelemetry.sdk")
                .longCounterBuilder(counterName)
                .setDescription(counterDescription)
                .build();

        longCounter.add(15, Labels.empty());

        List<Meter> meters = collector.getMeters();
        assertThat(meters).isNotNull();
        assertThat(meters.size()).isEqualTo(1);

        verifyMeter(meters.get(0), counterName, counterDescription, "1", 0, null, null, 15.0);

        longCounter.add(42, Labels.empty());

        meters = collector.getMeters();
        assertThat(meters).isNotNull();
        assertThat(meters.size()).isEqualTo(1);

        verifyMeter(meters.get(0), counterName, counterDescription, "1", 0, null, null, 57.0);
    }

    @Test
    void testMultiIncrementWithLabels() {
        final String counterName = "long-counter-multi-increment-with-labels";
        final String counterDescription = "Description of long-counter-multi-increment-with-labels";
        final String labelKey = "myKey";
        final String labelValue = "myValue";

        LongCounter longCounter = OpenTelemetry.getMeter("io.smallrye.opentelemetry.sdk")
                .longCounterBuilder(counterName)
                .setDescription(counterDescription)
                .setConstantLabels(Labels.of(labelKey, labelValue))
                .build();

        longCounter.add(15, Labels.empty());

        List<Meter> meters = collector.getMeters();
        assertThat(meters).isNotNull();
        assertThat(meters.size()).isEqualTo(1);

        verifyMeter(meters.get(0), counterName, counterDescription, "1", 1, labelKey, labelValue, 15.0);

        longCounter.add(42, Labels.empty());

        meters = collector.getMeters();
        assertThat(meters).isNotNull();
        assertThat(meters.size()).isEqualTo(1);

        verifyMeter(meters.get(0), counterName, counterDescription, "1", 1, labelKey, labelValue, 57.0);
    }

    @Test
    void testBind() {
        final String counterName = "long-counter-binding-no-labels";
        final String counterDescription = "Description of long-counter-binding-no-labels";
        final String labelKey = "myKey";
        final String labelValue = "myValue";

        LongCounter longCounter = OpenTelemetry.getMeter("io.smallrye.opentelemetry.sdk")
                .longCounterBuilder(counterName)
                .setDescription(counterDescription)
                .build();

        LongCounter.BoundLongCounter boundLongCounter = longCounter.bind(Labels.of(labelKey, labelValue));
        boundLongCounter.add(98);
        boundLongCounter.unbind();

        List<Meter> meters = collector.getMeters();
        assertThat(meters).isNotNull();
        assertThat(meters.size()).isEqualTo(1);

        verifyMeter(meters.get(0), counterName, counterDescription, "1", 1, labelKey, labelValue, 98.0);
    }

    @Test
    void testBindWithCommonLabels() {
        final String counterName = "long-counter-binding-common-labels";
        final String counterDescription = "Description of long-counter-binding-common-labels";
        final String labelKey1 = "myKey1";
        final String labelValue1 = "myValue1";
        final String labelKey2 = "myKey2";
        final String labelValue2 = "myValue2";

        LongCounter longCounter = OpenTelemetry.getMeter("io.smallrye.opentelemetry.sdk")
                .longCounterBuilder(counterName)
                .setDescription(counterDescription)
                .setConstantLabels(Labels.of(labelKey1, labelValue1))
                .build();

        LongCounter.BoundLongCounter boundLongCounter = longCounter.bind(Labels.of(labelKey2, labelValue2));
        boundLongCounter.add(31);
        boundLongCounter.unbind();

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
        assertThat(measure.getValue()).isEqualTo(31.0);

        assertThat(iterator.hasNext()).isFalse();
    }

    @Test
    void testMicrometerMixWithNoLabels() {
        final String otelCounterName = "otel-long-no-labels";
        final String otelCounterDescription = "Description of otel-long-no-labels";
        final String micrometerCounterName = "micrometer-long-no-labels";
        final String micrometerCounterDescription = "Description of micrometer-long-no-labels";

        // Create OTeL Counter
        LongCounter otelLongCounter = OpenTelemetry.getMeter("io.smallrye.opentelemetry.sdk")
                .longCounterBuilder(otelCounterName)
                .setDescription(otelCounterDescription)
                .build();

        // Create Micrometer Counter
        Counter micrometerCounter = Counter.builder(micrometerCounterName)
                .description(micrometerCounterDescription)
                .baseUnit("1")
                .register(OpenTelemetryMeterRegistry.INSTANCE);

        // Operate on counters
        otelLongCounter.add(3, Labels.empty());
        micrometerCounter.increment(4);

        // Verify meters
        List<Meter> meters = collector.getMeters();
        assertThat(meters).isNotNull();
        assertThat(meters.size()).isEqualTo(2);

        for (Meter meter : meters) {
            if (meter.getId().getName().equals(otelCounterName)) {
                verifyMeter(meter, otelCounterName, otelCounterDescription, "1", 0, null, null, 3.0);
            } else if (meter.getId().getName().equals(micrometerCounterName)) {
                verifyMeter(meter, micrometerCounterName, micrometerCounterDescription, "1", 0, null, null, 4.0);
            } else {
                fail("Found a meter that shouldn't be here: " + meter.getId().getName());
            }
        }
    }

    @Test
    void testMicrometerMixWithLabels() {
        final String otelCounterName = "otel-long-labels";
        final String otelCounterDescription = "Description of otel-long-labels";
        final String micrometerCounterName = "micrometer-long-labels";
        final String micrometerCounterDescription = "Description of micrometer-long-labels";
        final String labelKey = "myKey";
        final String labelValue = "myValue";

        // Create OTeL Counter
        LongCounter otelLongCounter = OpenTelemetry.getMeter("io.smallrye.opentelemetry.sdk")
                .longCounterBuilder(otelCounterName)
                .setDescription(otelCounterDescription)
                .setConstantLabels(Labels.of(labelKey, labelValue))
                .build();

        // Create Micrometer Counter
        Counter micrometerCounter = Counter.builder(micrometerCounterName)
                .description(micrometerCounterDescription)
                .baseUnit("1")
                .tag(labelKey, labelValue)
                .register(OpenTelemetryMeterRegistry.INSTANCE);

        // Operate on counters
        otelLongCounter.add(2, Labels.empty());
        micrometerCounter.increment(2);

        // Verify meters
        List<Meter> meters = collector.getMeters();
        assertThat(meters).isNotNull();
        assertThat(meters.size()).isEqualTo(2);

        for (Meter meter : meters) {
            if (meter.getId().getName().equals(otelCounterName)) {
                verifyMeter(meter, otelCounterName, otelCounterDescription, "1", 1, labelKey, labelValue, 2.0);
            } else if (meter.getId().getName().equals(micrometerCounterName)) {
                verifyMeter(meter, micrometerCounterName, micrometerCounterDescription, "1", 1, labelKey, labelValue, 2.0);
            } else {
                fail("Found a meter that shouldn't be here: " + meter.getId().getName());
            }
        }
    }

    @Test
    void testMicrometerMixSingleMeter() {
        final String counterName = "long-counter-only-one-no-labels";
        final String counterDescription = "Description of long-counter-only-one-no-labels";

        // Create OTeL Counter
        LongCounter otelLongCounter = OpenTelemetry.getMeter("io.smallrye.opentelemetry.sdk")
                .longCounterBuilder(counterName)
                .setDescription(counterDescription)
                .build();

        // Create Micrometer Counter
        Counter micrometerCounter = Counter.builder(counterName)
                .description(counterDescription)
                .baseUnit("1")
                .register(OpenTelemetryMeterRegistry.INSTANCE);

        // Operate on counters
        otelLongCounter.add(1, Labels.empty());
        micrometerCounter.increment();

        // Verify meters
        List<Meter> meters = collector.getMeters();
        assertThat(meters).isNotNull();
        assertThat(meters.size()).isEqualTo(1);

        verifyMeter(meters.get(0), counterName, counterDescription, "1", 0, null, null, 2.0);
    }

    @Test
    void testMicrometerMixSingleMeterWithLabels() {
        final String counterName = "long-counter-only-one-with-labels";
        final String counterDescription = "Description of long-counter-only-one-with-labels";
        final String labelKey = "myKey";
        final String labelValue = "myValue";

        // Create OTeL Counter
        LongCounter otelLongCounter = OpenTelemetry.getMeter("io.smallrye.opentelemetry.sdk")
                .longCounterBuilder(counterName)
                .setDescription(counterDescription)
                .build();

        // Create Micrometer Counter
        Counter micrometerCounter = Counter.builder(counterName)
                .description(counterDescription)
                .baseUnit("1")
                .tag(labelKey, labelValue)
                .register(OpenTelemetryMeterRegistry.INSTANCE);

        // Operate on counters
        otelLongCounter.add(1, Labels.of(labelKey, labelValue));
        micrometerCounter.increment();

        // Verify meters
        List<Meter> meters = collector.getMeters();
        assertThat(meters).isNotNull();
        assertThat(meters.size()).isEqualTo(1);

        verifyMeter(meters.get(0), counterName, counterDescription, "1", 1, labelKey, labelValue, 2.0);
    }

    private void verifyMeter(Meter meterToVerify, String name, String description,
            String baseUnit, int numTags, String labelKey, String labelValue,
            double value) {
        assertThat(meterToVerify).isNotNull();
        assertThat(meterToVerify.getId()).isNotNull();
        assertThat(meterToVerify.getId().getName()).isEqualTo(name);
        assertThat(meterToVerify.getId().getDescription()).isEqualTo(description);
        assertThat(meterToVerify.getId().getBaseUnit()).isEqualTo(baseUnit);

        if (numTags == 0) {
            assertThat(meterToVerify.getId().getTags().size()).isZero();
        } else {
            assertThat(meterToVerify.getId().getTags().size()).isEqualTo(numTags);
            assertThat(meterToVerify.getId().getTags().get(0)).isNotNull();
            assertThat(meterToVerify.getId().getTags().get(0).getKey()).isEqualTo(labelKey);
            assertThat(meterToVerify.getId().getTags().get(0).getValue()).isEqualTo(labelValue);
        }

        Iterator<Measurement> iterator = meterToVerify.measure().iterator();
        assertThat(iterator.hasNext()).isTrue();

        Measurement measure = iterator.next();
        assertThat(measure.getStatistic()).isEqualTo(Statistic.COUNT);
        assertThat(measure.getValue()).isEqualTo(value);

        assertThat(iterator.hasNext()).isFalse();
    }
}
