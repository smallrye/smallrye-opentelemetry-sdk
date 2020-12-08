package io.smallrye.opentelemetry.sdk.tck;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapPropagator;

class OpenTelemetryTest {

    @Test
    void testDefaultPropagator() {
        ContextPropagators contextPropagators = OpenTelemetry.getGlobalPropagators();
        assertThat(contextPropagators).isNotNull();
        assertThat(contextPropagators.getTextMapPropagator()).isNotNull();
        assertThat(contextPropagators.getTextMapPropagator()).isInstanceOf(TextMapPropagator.class);
    }

    @Test
    void testCustomPropagator() {
        OpenTelemetry.setGlobalPropagators(new ContextPropagators() {
            @Override
            public TextMapPropagator getTextMapPropagator() {
                return new CustomTextMapPropagator();
            }
        });

        ContextPropagators contextPropagators = OpenTelemetry.getGlobalPropagators();
        assertThat(contextPropagators).isNotNull();
        assertThat(contextPropagators.getTextMapPropagator()).isNotNull();
        assertThat(contextPropagators.getTextMapPropagator()).isInstanceOf(TextMapPropagator.class);
        assertThat(contextPropagators.getTextMapPropagator()).isInstanceOf(CustomTextMapPropagator.class);
    }

    static class CustomTextMapPropagator implements TextMapPropagator {
        @Override
        public List<String> fields() {
            return null;
        }

        @Override
        public <C> void inject(Context context, C carrier, Setter<C> setter) {

        }

        @Override
        public <C> Context extract(Context context, C carrier, Getter<C> getter) {
            return null;
        }
    }
}
