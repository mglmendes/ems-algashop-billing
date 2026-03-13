package com.algaworks.billing.infrastructure.creditcard.client.input;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FastpayCreditCardInput {

    private String tokenizedCard;
    private String customerCode;
}
