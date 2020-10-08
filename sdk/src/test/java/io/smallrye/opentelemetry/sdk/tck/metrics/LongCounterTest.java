package io.smallrye.opentelemetry.sdk.tck.metrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Statistic;
import io.opentelemetry.OpenTelemetry;
import io.opentelemetry.common.Labels;
import io.opentelemetry.metrics.LongCounter;
import io.smallrye.opentelemetry.sdk.metrics.OpenTelemetryMeterRegistry;

class LongCounterTest extends AbstractMetricTest {

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
                .build();

        // Create Micrometer Counter
        Counter micrometerCounter = Counter.builder(micrometerCounterName)
                .description(micrometerCounterDescription)
                .baseUnit("1")
                .tag(labelKey, labelValue)
                .register(OpenTelemetryMeterRegistry.INSTANCE);

        // Operate on counters
        otelLongCounter.add(2, Labels.of(labelKey, labelValue));
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
