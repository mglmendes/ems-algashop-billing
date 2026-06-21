package com.algaworks.billing.infrastructure.payment.fastpay.config;

import org.jspecify.annotations.Nullable;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.cloud.circuitbreaker.retry.CircuitBreakerRetryPolicy;
import org.springframework.cloud.circuitbreaker.retry.FrameworkRetryCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.algaworks.billing.infrastructure.payment.fastpay.config.SpringCircuitBreakerConfig.FASTPAY_PAYMENT_CB_ID;

@Component("circuitbreakers")
public class CustomFrameworkRetryCircuitBreakerHealthIndicator implements HealthIndicator {

    private final List<FrameworkRetryCircuitBreaker> circuitBreakers = new ArrayList<>();

    public CustomFrameworkRetryCircuitBreakerHealthIndicator(CircuitBreakerFactory circuitBreakerFactory) {
        circuitBreakers.add((FrameworkRetryCircuitBreaker) circuitBreakerFactory.create(FASTPAY_PAYMENT_CB_ID));

    }

    @Override
    public @Nullable Health health() {
        Map<String, Object> indicatorDetails = new HashMap<>();
        String indicatorStatus = "UP";
        Throwable lastException = null;

        for (FrameworkRetryCircuitBreaker circuitBreaker : circuitBreakers) {
            var policy = circuitBreaker.getConfig().getCircuitBreakerRetryPolicy();
            assert policy != null;
            var state = policy.getState();
            Map<String, Object> circuitBreakerDetails = new HashMap<>();
            circuitBreakerDetails.put("state", state);

            if (state == CircuitBreakerRetryPolicy.State.OPEN) {
                indicatorStatus = "DEGRADED";
                if (policy.getLastException() != null && policy.getLastException().getCause() != null) {
                    lastException = policy.getLastException().getCause();
                    circuitBreakerDetails.put("lastException", lastException.getMessage());
                } else {
                    circuitBreakerDetails.put("lastException", null);
                }
            }

            indicatorDetails.put(circuitBreaker.getId(), circuitBreakerDetails);

        }

        Health.Builder healthBuilder = Health.status(indicatorStatus).withDetails(indicatorDetails);

        if (indicatorStatus.equals("DEGRADED") && lastException != null) {
            healthBuilder.withException(lastException);
        }

        return healthBuilder.build();
    }
}
