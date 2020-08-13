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

        verifyMeter(meters.get(0), counterName, counterDescription, "1", 0, null, null, 2.0);
    }

    @Test
    void testWithUnit() {
        final String counterName = "my-counter-unit";
        final String counterDescription = "Description of my-counter-unit";

        DoubleCounter doubleCounter = OpenTelemetry.getMeter("io.smallrye.opentelemetry.sdk")
                .doubleCounterBuilder(counterName)
                .setDescription(counterDescription)
                .setUnit("2")
                .build();

        doubleCounter.add(3, Labels.empty());

        List<Meter> meters = collector.getMeters();
        assertThat(meters).isNotNull();
        assertThat(meters.size()).isEqualTo(1);

        verifyMeter(meters.get(0), counterName, counterDescription, "2", 0, null, null, 3.0);
    }

    @Test
    void testIncrementFail() {
        final String counterName = "my-counter-fail";
        final String counterDescription = "Description of my-counter-fail";

        DoubleCounter doubleCounter = OpenTelemetry.getMeter("io.smallrye.opentelemetry.sdk")
                .doubleCounterBuilder(counterName)
                .setDescription(counterDescription)
                .build();

        Labels labels = Labels.empty();
        assertThrows(IllegalArgumentException.class, () -> doubleCounter.add(-2, labels));

        List<Meter> meters = collector.getMeters();
        assertThat(meters).isNotNull();
        assertThat(meters.size()).isZero();
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

        verifyMeter(meters.get(0), counterName, counterDescription, "1", 1, labelKey, labelValue, 4.0);
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

        verifyMeter(meters.get(0), counterName, counterDescription, "1", 1, labelKey, labelValue, 6.0);
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
    void testMultiIncrementWithNoLabels() {
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

        verifyMeter(meters.get(0), counterName, counterDescription, "1", 0, null, null, 1.5);

        doubleCounter.add(4.2, Labels.empty());

        meters = collector.getMeters();
        assertThat(meters).isNotNull();
        assertThat(meters.size()).isEqualTo(1);

        verifyMeter(meters.get(0), counterName, counterDescription, "1", 0, null, null, 5.7);
    }

    @Test
    void testMultiIncrementWithLabels() {
        final String counterName = "my-counter-multi-increment-with-labels";
        final String counterDescription = "Description of my-counter-multi-increment-with-labels";
        final String labelKey = "myKey";
        final String labelValue = "myValue";

        DoubleCounter doubleCounter = OpenTelemetry.getMeter("io.smallrye.opentelemetry.sdk")
                .doubleCounterBuilder(counterName)
                .setDescription(counterDescription)
                .setConstantLabels(Labels.of(labelKey, labelValue))
                .build();

        doubleCounter.add(1.5, Labels.empty());

        List<Meter> meters = collector.getMeters();
        assertThat(meters).isNotNull();
        assertThat(meters.size()).isEqualTo(1);

        verifyMeter(meters.get(0), counterName, counterDescription, "1", 1, labelKey, labelValue, 1.5);

        doubleCounter.add(4.2, Labels.empty());

        meters = collector.getMeters();
        assertThat(meters).isNotNull();
        assertThat(meters.size()).isEqualTo(1);

        verifyMeter(meters.get(0), counterName, counterDescription, "1", 1, labelKey, labelValue, 5.7);
    }

    @Test
    void testBind() {
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
        boundDoubleCounter.unbind();

        List<Meter> meters = collector.getMeters();
        assertThat(meters).isNotNull();
        assertThat(meters.size()).isEqualTo(1);

        verifyMeter(meters.get(0), counterName, counterDescription, "1", 1, labelKey, labelValue, 3.1);
    }

    @Test
    void testBindWithCommonLabels() {
        final String counterName = "my-counter-binding-common-labels";
        final String counterDescription = "Description of my-counter-binding-common-labels";
        final String labelKey1 = "myKey1";
        final String labelValue1 = "myValue1";
        final String labelKey2 = "myKey2";
        final String labelValue2 = "myValue2";

        DoubleCounter doubleCounter = OpenTelemetry.getMeter("io.smallrye.opentelemetry.sdk")
                .doubleCounterBuilder(counterName)
                .setDescription(counterDescription)
                .setConstantLabels(Labels.of(labelKey1, labelValue1))
                .build();

        DoubleCounter.BoundDoubleCounter boundDoubleCounter = doubleCounter.bind(Labels.of(labelKey2, labelValue2));
        boundDoubleCounter.add(3.1);
        boundDoubleCounter.unbind();

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
        assertThat(measure.getValue()).isEqualTo(3.1);

        assertThat(iterator.hasNext()).isFalse();
    }

    @Test
    void testMicrometerMixWithNoLabels() {
        final String otelCounterName = "otel-counter-no-labels";
        final String otelCounterDescription = "Description of otel-counter-no-labels";
        final String micrometerCounterName = "micrometer-counter-no-labels";
        final String micrometerCounterDescription = "Description of micrometer-counter-no-labels";

        // Create OTeL Counter
        DoubleCounter otelDoubleCounter = OpenTelemetry.getMeter("io.smallrye.opentelemetry.sdk")
                .doubleCounterBuilder(otelCounterName)
                .setDescription(otelCounterDescription)
                .build();

        // Create Micrometer Counter
        Counter micrometerCounter = Counter.builder(micrometerCounterName)
                .description(micrometerCounterDescription)
                .baseUnit("1")
                .register(OpenTelemetryMeterRegistry.INSTANCE);

        // Operate on counters
        otelDoubleCounter.add(1, Labels.empty());
        micrometerCounter.increment();

        // Verify meters
        List<Meter> meters = collector.getMeters();
        assertThat(meters).isNotNull();
        assertThat(meters.size()).isEqualTo(2);

        for (Meter meter : meters) {
            if (meter.getId().getName().equals(otelCounterName)) {
                verifyMeter(meter, otelCounterName, otelCounterDescription, "1", 0, null, null, 1.0);
            } else if (meter.getId().getName().equals(micrometerCounterName)) {
                verifyMeter(meter, micrometerCounterName, micrometerCounterDescription, "1", 0, null, null, 1.0);
            } else {
                fail("Found a meter that shouldn't be here: " + meter.getId().getName());
            }
        }
    }

    @Test
    void testMicrometerMixWithLabels() {
        final String otelCounterName = "otel-counter-labels";
        final String otelCounterDescription = "Description of otel-counter-labels";
        final String micrometerCounterName = "micrometer-counter-labels";
        final String micrometerCounterDescription = "Description of micrometer-counter-labels";
        final String labelKey = "myKey";
        final String labelValue = "myValue";

        // Create OTeL Counter
        DoubleCounter otelDoubleCounter = OpenTelemetry.getMeter("io.smallrye.opentelemetry.sdk")
                .doubleCounterBuilder(otelCounterName)
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
        otelDoubleCounter.add(2, Labels.empty());
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
        final String counterName = "my-counter-only-one-no-labels";
        final String counterDescription = "Description of my-counter-only-one-no-labels";

        // Create OTeL Counter
        DoubleCounter otelDoubleCounter = OpenTelemetry.getMeter("io.smallrye.opentelemetry.sdk")
                .doubleCounterBuilder(counterName)
                .setDescription(counterDescription)
                .build();

        // Create Micrometer Counter
        Counter micrometerCounter = Counter.builder(counterName)
                .description(counterDescription)
                .baseUnit("1")
                .register(OpenTelemetryMeterRegistry.INSTANCE);

        // Operate on counters
        otelDoubleCounter.add(1, Labels.empty());
        micrometerCounter.increment();

        // Verify meters
        List<Meter> meters = collector.getMeters();
        assertThat(meters).isNotNull();
        assertThat(meters.size()).isEqualTo(1);

        verifyMeter(meters.get(0), counterName, counterDescription, "1", 0, null, null, 2.0);
    }

    @Test
    void testMicrometerMixSingleMeterWithLabels() {
        final String counterName = "my-counter-only-one-with-labels";
        final String counterDescription = "Description of my-counter-only-one-with-labels";
        final String labelKey = "myKey";
        final String labelValue = "myValue";

        // Create OTeL Counter
        DoubleCounter otelDoubleCounter = OpenTelemetry.getMeter("io.smallrye.opentelemetry.sdk")
                .doubleCounterBuilder(counterName)
                .setDescription(counterDescription)
                .build();

        // Create Micrometer Counter
        Counter micrometerCounter = Counter.builder(counterName)
                .description(counterDescription)
                .baseUnit("1")
                .tag(labelKey, labelValue)
                .register(OpenTelemetryMeterRegistry.INSTANCE);

        // Operate on counters
        otelDoubleCounter.add(1, Labels.of(labelKey, labelValue));
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
