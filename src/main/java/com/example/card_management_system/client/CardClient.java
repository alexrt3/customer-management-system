package com.example.card_management_system.client;

import com.example.card_management_system.dto.v1.CardResponseV1DTO;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Component
public class CardClient {

    private final RestTemplate restTemplate;

    @Value("${card.ms.url}")
    private final String cardServiceUrl;

    public List<CardResponseV1DTO> getCardsByCustomerId(String customerId) {
        String serviceUrl = String.format("%s/api/cards/v1/%s/all", cardServiceUrl, customerId);

        try {
            CardResponseV1DTO[] response = restTemplate.getForObject(serviceUrl, CardResponseV1DTO[].class);
            if (response != null) {
                return Arrays.asList(response);
            }
        } catch (Exception e) {
            log.error("Could not get cards for customer: {} from card service", customerId);
        }
        return List.of();
    }
}
