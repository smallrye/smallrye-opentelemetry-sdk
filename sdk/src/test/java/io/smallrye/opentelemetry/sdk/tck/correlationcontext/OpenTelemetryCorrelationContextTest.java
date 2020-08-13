package io.smallrye.opentelemetry.sdk.tck.correlationcontext;

import static org.assertj.core.api.Assertions.assertThat;

import io.opentelemetry.OpenTelemetry;
import io.opentelemetry.correlationcontext.CorrelationContextManager;
import io.opentelemetry.correlationcontext.DefaultCorrelationContextManager;

class OpenTelemetryCorrelationContextTest {

    //TODO Uncomment when correlation context present
    //    @Test
    void testCorrelationContextProvderLookup() {
        CorrelationContextManager correlationContextManager = OpenTelemetry.getCorrelationContextManager();
        assertThat(correlationContextManager).isNotNull();
        assertThat(correlationContextManager).isNotInstanceOf(DefaultCorrelationContextManager.class);
    }
}
