package com.algaworks.billing.application.creditcard.query.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class TokenizedCreditCardInput {
    private UUID customerId;
    @NotBlank
    private String tokenizedCard;
}
