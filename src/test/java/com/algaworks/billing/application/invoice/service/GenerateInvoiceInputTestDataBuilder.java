package com.algaworks.billing.application.invoice.service;

import com.algaworks.billing.application.invoice.input.*;
import com.algaworks.billing.domain.model.invoice.enums.PaymentMethod;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class GenerateInvoiceInputTestDataBuilder{

    public static GenerateInvoiceInput.GenerateInvoiceInputBuilder anInput() {
        return GenerateInvoiceInput.builder()
                .orderId("ABC100")
                .customerId(UUID.randomUUID())
                .paymentSettings(PaymentSettingsInput.builder()
                        .method(PaymentMethod.CREDIT_CARD)
                        .creditCardId(UUID.randomUUID())
                        .build())
                .payer(PayerData.builder()
                        .fullName("John Doe")
                        .document("111.222.333-44")
                        .phone("11-99999-8888")
                        .email("john.doe@email.com")
                        .address(AddressData.builder()
                                .street("Street Name")
                                .number("123")
                                .neighborhood("Neighborhood")
                                .city("City")
                                .state("State")
                                .zipCode("12345-678")
                                .build())
                        .build())
                .items(List.of(LineItemInput.builder()
                        .name("Product 1")
                        .amount(new BigDecimal("200.00"))
                        .build()));
    }
}