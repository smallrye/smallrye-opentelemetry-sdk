package io.smallrye.opentelemetry.sdk.metrics.utils;

import java.util.ArrayList;
import java.util.List;

import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.opentelemetry.api.common.Labels;

public class LabelConverter {
    private LabelConverter() {
        // Prevent direct instantiation
    }

    public static Tags toTags(Labels labels) {
        List<Tag> tags = new ArrayList<>(labels.size());
        labels.forEach((k, v) -> tags.add(Tag.of(k, v)));
        return Tags.of(tags);
    }
}
