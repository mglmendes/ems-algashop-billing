package com.algaworks.billing.infrastructure.payment.fastpay.service;

import com.algaworks.billing.domain.model.creditcard.entity.CreditCard;
import com.algaworks.billing.domain.model.creditcard.repository.CreditCardRepository;
import com.algaworks.billing.domain.model.invoice.entity.Address;
import com.algaworks.billing.domain.model.invoice.entity.Payer;
import com.algaworks.billing.domain.model.invoice.payment.entity.Payment;
import com.algaworks.billing.domain.model.invoice.payment.request.PaymentRequest;
import com.algaworks.billing.domain.model.invoice.payment.service.PaymentGatewayService;
import com.algaworks.billing.infrastructure.payment.fastpay.client.FastpayPaymentAPIClient;
import com.algaworks.billing.infrastructure.payment.fastpay.enums.FastpayPaymentMethod;
import com.algaworks.billing.infrastructure.payment.fastpay.enums.FastpayPaymentStatus;
import com.algaworks.billing.infrastructure.payment.fastpay.input.FastpayPaymentInput;
import com.algaworks.billing.infrastructure.payment.fastpay.model.FastpayPaymentModel;
import com.algaworks.billing.infrastructure.payment.fastpay.util.FastpayEnumConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@ConditionalOnProperty(name = "algashop.integrations.payment.provider", havingValue = "FASTPAY")
@RequiredArgsConstructor
public class PaymentGatewayFastpayImpl implements PaymentGatewayService {

    private final FastpayPaymentAPIClient fastpayPaymentAPIClient;
    private CreditCardRepository creditCardRepository;

    @Override
    public Payment capture(PaymentRequest request) {
        FastpayPaymentInput input = convertToInput(request);
        FastpayPaymentModel response = fastpayPaymentAPIClient.capture(input
        );

        return convertToPayment(response);
    }

    @Override
    public Payment findByCode(String gatewayCode) {
        FastpayPaymentModel response = fastpayPaymentAPIClient.findById(gatewayCode);
        return convertToPayment(response);
    }

    private FastpayPaymentInput convertToInput(PaymentRequest request) {
        Payer payer = request.getPayer();
        Address payerAddress = payer.getAddress();
        var builder = FastpayPaymentInput.builder()
                .referenceCode(request.getInvoiceId().toString())
                .totalAmount(request.getAmount())
                .creditCardId(request.getCreditCardId().toString())
                .fullName(payer.getFullName())
                .document(payer.getDocument())
                .phone(payer.getPhone())
                .addressLine1(payerAddress.getState() + ", " + payerAddress.getNumber())
                .addressLine2(payerAddress.getComplement())
                .zipCode(payerAddress.getZipCode())
                .replyToUrl("http://urlqualquer");

        switch (request.getMethod()) {
            case CREDIT_CARD -> {
                builder.method(FastpayPaymentMethod.CREDIT.name());
                CreditCard creditCard = creditCardRepository.findById(request.getCreditCardId()).orElseThrow(
                        () -> new IllegalArgumentException("Credit card id not found")
                );
                builder.creditCardId(creditCard.getGatewayCode());
            }
            case GATEWAY_BALANCE -> {
                builder.method(FastpayPaymentMethod.GATEWAY_BALANCE.name());
            }
        }
        return builder.build();
    }

    private Payment convertToPayment(FastpayPaymentModel response) {
        var builder = Payment.builder()
                .gatewayCode(response.getId())
                .invoiceId(UUID.fromString(response.getReferenceCode()));

        FastpayPaymentMethod fastpayPaymentMethod;
        try {
            fastpayPaymentMethod = FastpayPaymentMethod.valueOf(response.getMethod());
        } catch (Exception e) {
            throw new IllegalArgumentException("Unknown payment method " + response.getMethod() );
        }

        FastpayPaymentStatus fastpayPaymentStatus;
        try {
            fastpayPaymentStatus = FastpayPaymentStatus.valueOf(response.getStatus());
        } catch (Exception e) {
            throw new IllegalArgumentException("Unknown payment status " + response.getStatus() );
        }

        builder.method(FastpayEnumConverter.convert(fastpayPaymentMethod));
        builder.status(FastpayEnumConverter.convert(fastpayPaymentStatus));

        return builder.build();
    }
}
