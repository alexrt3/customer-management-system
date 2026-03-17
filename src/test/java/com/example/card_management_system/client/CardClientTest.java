package com.example.card_management_system.client;

import com.example.card_management_system.config.RestTemplateConfig;
import com.example.card_management_system.dto.v1.CardResponseV1DTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(CardClient.class)
@Import(RestTemplateConfig.class)
@TestPropertySource(properties = {
        "card.ms.url=http://localhost:8080"
})
class CardClientTest {

    @Autowired
    private CardClient cardClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer fake-token");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    void getCardsBuyCustomerId_shouldreturnListOfCards_whenSuccessful() throws Exception {

        String customerId = "cust-123";
        String expectedUrl = "http://localhost:8080/api/cards/v1/cust-123/all";

        CardResponseV1DTO mockCard = CardResponseV1DTO.builder()
                                                      .accountNumber("account-123")
                                                      .cardNumber("4444333322221111")
                                                      .build();

        List<CardResponseV1DTO> mockResponse = List.of(mockCard);

        this.mockServer.expect(requestTo(expectedUrl))
                       .andRespond(withSuccess(objectMapper.writeValueAsString(mockResponse), MediaType.APPLICATION_JSON));

        List<CardResponseV1DTO> actualCards = cardClient.getCardsByCustomerId(customerId);

        assertNotNull(actualCards);
        assertThat(actualCards).hasSize(1);
        assertThat("4444333322221111").isEqualTo(actualCards.get(0).getCardNumber());
    }
}