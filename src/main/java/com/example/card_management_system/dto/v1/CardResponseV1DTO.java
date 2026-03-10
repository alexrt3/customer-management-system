package com.example.card_management_system.dto.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardResponseV1DTO {

    private String accountId;
    private String accountNumber;
    private String cardNumber;
    private String cardType;
    private String expiryDate;
    private String cardHolderName;
    private String active;
    private String creditLimit;
    private String securityCode;
    private String customerId;
    private String digitalCardOnly;
    private String dailyLimit;

}
