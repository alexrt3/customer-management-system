package com.example.card_management_system.validation.v1;

import com.example.card_management_system.validation.CardLimitMatchValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CardLimitMatchValidatorV1.class)
public @interface CardLimitMatchV1 {
    String message() default "Credit limit is only allowed for CREDIT cards";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
