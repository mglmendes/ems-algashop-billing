package com.algaworks.billing.infrastructure.creditcard.service.fastpay;

import com.algaworks.billing.domain.model.creditcard.entity.LimitedCreditCard;
import com.algaworks.billing.domain.model.creditcard.service.CreditCardProviderService;
import com.algaworks.billing.infrastructure.creditcard.client.FastpayCreditCardAPIClient;
import com.algaworks.billing.infrastructure.creditcard.client.input.FastpayCreditCardInput;
import com.algaworks.billing.infrastructure.creditcard.client.output.FastpayCreditCardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "algashop.integrations.payment.provider", havingValue = "FASTPAY")
@RequiredArgsConstructor
public class CreditCardProviderServiceFastpayImpl implements CreditCardProviderService {

    private final FastpayCreditCardAPIClient fastpayClient;


    @Override
    public LimitedCreditCard register(UUID customerId, String tokenizedCard) {
        FastpayCreditCardInput input = FastpayCreditCardInput.builder()
                .tokenizedCard(tokenizedCard)
                .customerCode(customerId.toString())
                .build();

        return toLimitedCreditCard(fastpayClient.create(input));
    }

    @Override
    public Optional<LimitedCreditCard> findById(String gatewayCode) {
        return Optional.of(toLimitedCreditCard(fastpayClient.findById(gatewayCode)));
    }

    @Override
    public void delete(String gatewayCode) {
        fastpayClient.delete(gatewayCode);
    }

    private static LimitedCreditCard toLimitedCreditCard(FastpayCreditCardResponse fastpayCreditCardResponse) {
        return LimitedCreditCard.builder()
                .brand(fastpayCreditCardResponse.getBrand())
                .expirationMonth(fastpayCreditCardResponse.getExpMonth())
                .expirationYear(fastpayCreditCardResponse.getExpYear())
                .lastNumbers(fastpayCreditCardResponse.getLastNumbers())
                .build();
    }
}
