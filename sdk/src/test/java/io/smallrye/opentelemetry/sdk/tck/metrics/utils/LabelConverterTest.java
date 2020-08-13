package io.smallrye.opentelemetry.sdk.tck.metrics.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Iterator;

import org.junit.jupiter.api.Test;

import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.opentelemetry.common.Labels;
import io.smallrye.opentelemetry.sdk.metrics.utils.LabelConverter;

class LabelConverterTest {
    @Test
    void testToTagsWithNoLabels() {
        Tags tags = LabelConverter.toTags(Labels.empty());
        assertThat(tags).isNotNull();
        assertThat(tags.iterator().hasNext()).isFalse();
    }

    @Test
    void testToTagsWithLabels() {
        final String key = "my-key";
        final String value = "my-value";

        Tags tags = LabelConverter.toTags(Labels.of(key, value));
        assertThat(tags).isNotNull();

        Iterator<Tag> tagIterator = tags.iterator();
        assertThat(tagIterator).isNotNull();
        assertThat(tagIterator.hasNext()).isTrue();

        Tag tag = tagIterator.next();
        assertThat(tag).isNotNull();
        assertThat(tag.getKey()).isEqualTo(key);
        assertThat(tag.getValue()).isEqualTo(value);
    }
}
