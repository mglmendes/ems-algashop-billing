package com.algaworks.billing.infrastructure.creditcard.client.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FastpayCreditCardResponse {

    private String id;
    private String lastNumbers;
    private Integer expMonth;
    private Integer expYear;
    private String brand;
}
