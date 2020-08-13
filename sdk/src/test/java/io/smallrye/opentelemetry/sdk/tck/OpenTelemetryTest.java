package io.smallrye.opentelemetry.sdk.tck;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.grpc.Context;
import io.opentelemetry.OpenTelemetry;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.HttpTextFormat;

class OpenTelemetryTest {

    @Test
    void testDefaultPropagator() {
        ContextPropagators contextPropagators = OpenTelemetry.getPropagators();
        assertThat(contextPropagators).isNotNull();
        assertThat(contextPropagators.getHttpTextFormat()).isNotNull();
        assertThat(contextPropagators.getHttpTextFormat()).isInstanceOf(HttpTextFormat.class);
    }

    @Test
    void testCustomPropagator() {
        OpenTelemetry.setPropagators(new ContextPropagators() {
            @Override
            public HttpTextFormat getHttpTextFormat() {
                return new CustomHttpTextFormat();
            }
        });

        ContextPropagators contextPropagators = OpenTelemetry.getPropagators();
        assertThat(contextPropagators).isNotNull();
        assertThat(contextPropagators.getHttpTextFormat()).isNotNull();
        assertThat(contextPropagators.getHttpTextFormat()).isInstanceOf(HttpTextFormat.class);
        assertThat(contextPropagators.getHttpTextFormat()).isInstanceOf(CustomHttpTextFormat.class);
    }

    static class CustomHttpTextFormat implements HttpTextFormat {
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
