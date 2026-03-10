package com.example.card_management_system.validation.v1;

import com.example.card_management_system.dto.v1.CreateCardRequestV1DTO;
import com.example.card_management_system.entity.Card;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CardLimitMatchValidatorV1 implements ConstraintValidator<CardLimitMatchV1, CreateCardRequestV1DTO> {

    @Override
    public boolean isValid(CreateCardRequestV1DTO requestDTO, ConstraintValidatorContext context) {
        if (requestDTO == null) {
            return true;
        }

        boolean isCreditCard = Card.CardType.CREDIT.name().equals(requestDTO.getCardType());
        boolean hasCreditLimit = requestDTO.getCreditLimit() != null && !requestDTO.getCreditLimit().isBlank();

        if (!isCreditCard && hasCreditLimit) {
            return false;
        } else {
            return true;
        }
    }
}
