package io.smallrye.opentelemetry.sdk.metrics.utils;

import java.util.concurrent.atomic.AtomicReference;

import io.micrometer.core.instrument.Tags;
import io.opentelemetry.common.Labels;

public class LabelConverter {
    private LabelConverter() {
        // Prevent direct instantiation
    }

    public static Tags toTags(Labels labels) {
        AtomicReference<Tags> tags = new AtomicReference<>(Tags.empty());
        labels.forEach((k, v) -> tags.set(tags.get().and(k, v)));
        return tags.get();
    }
}
