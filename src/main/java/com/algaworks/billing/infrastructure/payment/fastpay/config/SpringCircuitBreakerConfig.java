package com.algaworks.billing.infrastructure.payment.fastpay.config;

import com.algaworks.billing.presentention.exception.BadGatewayException;
import com.algaworks.billing.presentention.exception.FastpayPaymentCaptureFailed;
import com.algaworks.billing.presentention.exception.GatewayTimeoutException;
import org.springframework.cloud.circuitbreaker.retry.FrameworkRetryCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.retry.RetryPolicy;

import java.time.Duration;

@Configuration
public class SpringCircuitBreakerConfig {

    public static final String FASTPAY_PAYMENT_CB_ID = "fastpayPaymentCB";

    @Bean
    public Customizer<FrameworkRetryCircuitBreakerFactory> defaultCustomizer() {
        RetryPolicy retryPolicy = RetryPolicy.builder()
                .maxRetries(3)
                .multiplier(2)
                .delay(Duration.ofSeconds(3))
                .includes(GatewayTimeoutException.class, BadGatewayException.ServerErrorException.class)
                .excludes(FastpayPaymentCaptureFailed.class)
                .build();

        return factory -> factory.configure(builder -> builder
                        .retryPolicy(retryPolicy)
                        .openTimeout(Duration.ofSeconds(30))
                        .resetTimeout(Duration.ofSeconds(60))
                        .build(),
                FASTPAY_PAYMENT_CB_ID
        );
    }
}