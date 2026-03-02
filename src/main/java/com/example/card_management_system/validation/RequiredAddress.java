package com.example.card_management_system.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RequiredAddressValidator.class)
public @interface RequiredAddress {

    String message() default "State, City, and Zipcode cannot be null when updating Address";

    Class<?>[] groups() default{};

    Class <? extends Payload>[] payload() default{};

}
