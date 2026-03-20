package com.example.card_management_system.client;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.example.card_management_system.dto.v1.CardResponseV1DTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "CardService")
public class CardClientPactTest {

    @Pact(consumer = "CustomerService")
    public V4Pact createCardListPact(PactDslWithProvider builder) {
        return builder.given("Customer 3f8c2c4e-9b8a-4e2d-9f3b-71d4c9a6c812 has active cards")
                      .uponReceiving("A GET request for customer cards")
                      .path("/api/cards/v1/3f8c2c4e-9b8a-4e2d-9f3b-71d4c9a6c812/all")
                      .method("GET")
                      .headers("Authorization", "Bearer fake-pact-token")
                      .willRespondWith()
                      .status(200)
                      .body(LambdaDsl.newJsonArray((array) -> array.object((obj) -> {
                          obj.stringType("accountId", "3f8c2c4e-9b8a-4e2d-9f3b-71d4c9a6c812");
                          obj.stringType("accountNumber", "3f8c2c4e-9b8a-4e2d-9f3b-000000000001");
                          obj.stringType("cardNumber", "1111222233334444");
                          obj.stringType("cardType", "CREDIT");
                          obj.stringType("cardHolderName", "John");
                          obj.stringType("expiryDate", "203012");
                          obj.stringType("active", "true");
                          obj.stringType("creditLimit", "5000.00");
                      })).build()).toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "createCardListPact")
    void verifyCardClientRetrieval(MockServer mockServer) {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .defaultHeader("Authorization", "Bearer fake-pact-token")
                .build();

        CardClient cardClient = new CardClient(restTemplate, mockServer.getUrl());

        List<CardResponseV1DTO> result = cardClient.getCardsByCustomerId("3f8c2c4e-9b8a-4e2d-9f3b-71d4c9a6c812");

        assertNotNull(result);
        assertThat(result).hasSize(1);
    }
}



