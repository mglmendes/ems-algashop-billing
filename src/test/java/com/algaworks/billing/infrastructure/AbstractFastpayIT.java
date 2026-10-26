package com.algaworks.billing.infrastructure;

import com.algaworks.billing.domain.model.creditcard.entity.LimitedCreditCard;
import com.algaworks.billing.infrastructure.creditcard.service.fastpay.CreditCardProviderServiceFastpayImpl;
import com.algaworks.billing.infrastructure.creditcard.service.fastpay.client.FastpayCreditCardTokenizationAPIClient;
import com.algaworks.billing.infrastructure.creditcard.service.fastpay.config.FastpayCreditCardTokenizationAPIClientConfig;
import com.algaworks.billing.infrastructure.creditcard.service.fastpay.input.FastpayTokenizationInput;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.ClasspathFileSource;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.extension.responsetemplating.TemplateEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Year;
import java.util.Collections;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

@Import(FastpayCreditCardTokenizationAPIClientConfig.class)
public abstract class AbstractFastpayIT {

    @MockitoBean
    private JwtDecoder jwtDecoder;

    public static final String SRC_TEST_RESOURCES_WIREMOCK_FASTPAY = "src/test/resources/wiremock/fastpay";
    @Autowired
    protected FastpayCreditCardTokenizationAPIClient tokenizationAPIClient;;

    @Autowired
    protected CreditCardProviderServiceFastpayImpl creditCardProvider;

    protected static final UUID validCustomerId = UUID.randomUUID();
    protected static final String alwaysPaidCardNumber = "4622943127011022";

    protected static WireMockServer wireMockFastpay;

    public static void startMock() {
        wireMockFastpay = new WireMockServer(options()
                .port(8788)
                .usingFilesUnderDirectory(SRC_TEST_RESOURCES_WIREMOCK_FASTPAY)
                .extensions(new ResponseTemplateTransformer(
                        TemplateEngine.defaultTemplateEngine(),
                        true,
                        new ClasspathFileSource(SRC_TEST_RESOURCES_WIREMOCK_FASTPAY),
                        Collections.emptyList()
                )));

        wireMockFastpay.start();
    }

    public static void stopMock() {
        wireMockFastpay.stop();
    }

    protected LimitedCreditCard registerCard() {
        FastpayTokenizationInput input = FastpayTokenizationInput.builder()
                .number(alwaysPaidCardNumber)
                .cvv("234")
                .holderName("Miguel")
                .holderDocument("22244411142")
                .expMonth(3)
                .expYear(Year.now().plusYears(5).getValue())
                .build();
        var tokenizedCard = tokenizationAPIClient.tokenized(input);
        return creditCardProvider.register(validCustomerId, tokenizedCard.getTokenizedCard());
    }
}
