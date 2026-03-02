package com.example.card_management_system.validation;


import com.example.card_management_system.dto.CardUpdateDTO;
import com.example.card_management_system.dto.CustomerUpdateDTO;
import com.example.card_management_system.entity.Customer;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RequiredAddressValidator implements ConstraintValidator<RequiredAddress, CustomerUpdateDTO> {

    @Override
    public boolean isValid(CustomerUpdateDTO requestDTO, ConstraintValidatorContext context) {
        if (requestDTO == null) {
            return true;
        }

        boolean isAddressLine1Updated = requestDTO.getAddressLine1() != null;
        boolean hasEmptyAddressField = requestDTO.getState() == null || requestDTO.getCityName() == null || requestDTO.getZipCode() == null;

        if (isAddressLine1Updated && hasEmptyAddressField) {
            return false;
        } else {
            return true;
        }

    }
}