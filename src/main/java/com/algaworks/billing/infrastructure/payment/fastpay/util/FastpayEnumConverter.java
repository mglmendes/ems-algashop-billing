package com.algaworks.billing.infrastructure.payment.fastpay.util;

import com.algaworks.billing.domain.model.invoice.enums.PaymentMethod;
import com.algaworks.billing.domain.model.invoice.payment.enums.PaymentStatus;
import com.algaworks.billing.infrastructure.payment.fastpay.enums.FastpayPaymentMethod;
import com.algaworks.billing.infrastructure.payment.fastpay.enums.FastpayPaymentStatus;
import org.springframework.stereotype.Component;


@Component
public class FastpayEnumConverter {
    public static PaymentMethod convert(FastpayPaymentMethod method) {
        return switch (method) {
            case CREDIT -> PaymentMethod.CREDIT_CARD;
            case GATEWAY_BALANCE -> PaymentMethod.GATEWAY_BALANCE;
        };
    }

    public static PaymentStatus convert(FastpayPaymentStatus status) {
        return switch (status) {
            case PENDING -> PaymentStatus.PENDING;
            case PROCESSING -> PaymentStatus.PROCESSING;
            case FAILED, CANCELED -> PaymentStatus.FAILED;
            case PAID -> PaymentStatus.PAID;
            case REFUNDED -> PaymentStatus.REFUNDED;
        };
    }
}
