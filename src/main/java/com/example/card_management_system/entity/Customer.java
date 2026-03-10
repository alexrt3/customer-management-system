package com.example.card_management_system.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name="customer")
@Getter
@Setter
@NoArgsConstructor(access= AccessLevel.PUBLIC, force = true)
@AllArgsConstructor
@Builder
public class Customer {

    @Column(name="customerId")
    @Id
    //@GeneratedValue
    private UUID customerId;

    @NotBlank
    @Size(min = 2, max = 50)
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 50)
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Size(max = 1)
    @Column(name = "middle_initial")
    private String middleInitial;

    @NotBlank
    @Email
    @Column(name = "email_address", nullable = false, unique = true)
    private String emailAddress;

    @NotBlank
    @Column(name = "phone_number", nullable = false, unique = true,  length = 15)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "phone_type")
    private PhoneType phoneType;

    @NotBlank
    @Column(name = "address_line_1", nullable = false)
    private String addressLine1;

    @Column(name = "address_line_2")
    private String addressLine2;

    @NotBlank
    @Column(name = "city_name", nullable = false)
    private String cityName;

    @NotBlank
    @Size(min = 2, max = 2)
    @Column(name = "state", nullable = false, length = 2)
    private String state;

    @NotBlank
    @Column(name = "zip_code", nullable = false, length = 5)
    private String zipcode;


    //CARD TYPE ENUM
    public enum PhoneType{
        MOBILE,
        HOME,
        WORK
    }



}
