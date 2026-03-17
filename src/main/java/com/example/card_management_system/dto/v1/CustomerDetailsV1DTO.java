package com.example.card_management_system.dto.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDetailsV1DTO {

    private String firstName;
    private String lastName;
    private String email;

    private List<CardResponseV1DTO> cards;
}
