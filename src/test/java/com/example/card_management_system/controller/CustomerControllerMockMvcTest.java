package com.example.card_management_system.controller;

import com.example.card_management_system.dto.CreateCustomerRequestDTO;
import com.example.card_management_system.dto.CustomerResponseDTO;
import com.example.card_management_system.dto.CustomerUpdateDTO;
import com.example.card_management_system.dto.v1.CardResponseV1DTO;
import com.example.card_management_system.dto.v1.CustomerDetailsV1DTO;
import com.example.card_management_system.entity.Customer;
import com.example.card_management_system.mapper.CustomerMapper;
import com.example.card_management_system.repository.CustomerRepository;
import com.example.card_management_system.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(properties = {
        "card.ms.url=http://localhost:8080"
})
public class CustomerControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @MockitoBean
    private CustomerService customerService;

    @Autowired
    private CustomerMapper customerMapper;


    private Customer testCustomer;

    @BeforeEach
    public void setUp() {
        // Initialize test data before each test method
        UUID customerId = UUID.randomUUID();

        testCustomer = Customer.builder()
                               .customerId(customerId)
                               .firstName("firstname")
                               .lastName("lastname")
                               .middleInitial("E")
                               .emailAddress("nnn@gmail.com")
                               .phoneNumber("3121231234")
                               .phoneType(Customer.PhoneType.MOBILE)
                               .addressLine1("Street")
                               .addressLine2("apt")
                               .cityName("chi")
                               .state("IL")
                               .zipcode("60606")
                               .build();
        //existing customer the card will be created for
        customerRepository.save(testCustomer);
    }


    @Test
    void shouldCreateNewCustomer() throws Exception {

        CreateCustomerRequestDTO customerRequest = CreateCustomerRequestDTO.builder()
                                                                           .firstName("firstname")
                                                                           .lastName("lastname")
                                                                           .middleInitial("E")
                                                                           .emailAddress("nnn@gmail.com")
                                                                           .phoneNumber("123123123121342")
                                                                           .phoneType("MOBILE")
                                                                           .addressLine1("Street")
                                                                           .addressLine2("apt")
                                                                           .cityName("chi")
                                                                           .state("IL")
                                                                           .zipCode("60606")
                                                                           .build();

        CustomerResponseDTO customerResponse = CustomerResponseDTO.builder()
                                                                  .customerID(UUID.randomUUID().toString())
                                                                  .firstName("firstname")
                                                                  .emailAddress("nnn@gmail.com")
                                                                  .build();

        when(customerService.createNewCustomer(any(CreateCustomerRequestDTO.class))).thenReturn(customerResponse);

        mockMvc.perform(post("/api/customers/create")
                       .with(jwt().jwt(builder -> builder.subject("test-user"))
                                  .authorities(new SimpleGrantedAuthority("SCOPE_ROLE_CLIENT")))
                       .contentType(MediaType.APPLICATION_JSON)
                       //Jackson Object Mapper to Json String
                       .content(objectMapper.writeValueAsString(customerRequest)))
               .andDo(print())
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.firstName").value("firstname"))
               .andExpect(jsonPath("$.emailAddress").value(("nnn@gmail.com")));
    }

    @Test
    void shouldThrowBadRequestException_WhenCreatingCustomerWithWrongStateField() throws Exception {
        customerRepository.deleteAll();

        CreateCustomerRequestDTO customerRequest = CreateCustomerRequestDTO.builder()
                                                                           .firstName("firstname")
                                                                           .lastName("lastname")
                                                                           .middleInitial("E")
                                                                           .emailAddress("nnn1@gmail.com")
                                                                           .phoneNumber("123123123121342")
                                                                           .phoneType("MOBILE")
                                                                           .addressLine1("Street")
                                                                           .addressLine2("apt")
                                                                           .cityName("chi")
                                                                           .state("ILL")//State has to be exactly 2 digits
                                                                           .zipCode("60606")
                                                                           .build();

        mockMvc.perform(post("/api/customers/create")
                       .with(jwt().jwt(builder -> builder.subject("test-user"))
                                  .authorities(new SimpleGrantedAuthority("SCOPE_ROLE_CLIENT")))
                       .contentType(MediaType.APPLICATION_JSON)
                       //Jackson Object Mapper to Json String
                       .content(objectMapper.writeValueAsString(customerRequest)))
               .andDo(print()).andExpect(status().isBadRequest());

        assertThat(customerRepository.count()).isEqualTo(0);
    }

    @Test
    void shouldRetrieveCustomer_GivenValidCustomerId() throws Exception {
        UUID customerId = UUID.randomUUID();

        CustomerResponseDTO customerResponse = customerMapper.customerToResponseDto(testCustomer);

        when(customerService.getCustomerById(customerId.toString())).thenReturn(customerResponse);

        mockMvc.perform(get("/api/customers/find/{customerId}", customerId)
                       .with(jwt().jwt(builder -> builder.subject("test-user"))
                                  .authorities(new SimpleGrantedAuthority("SCOPE_ROLE_CLIENT"))))
               .andExpect(status().isOk())
               .andDo(print())
               .andExpect(jsonPath("$.phoneNumber", is(testCustomer.getPhoneNumber())))
               .andExpect(jsonPath("$.firstName", is(testCustomer.getFirstName())))
               .andExpect(jsonPath("$.lastName", is(testCustomer.getLastName())));

    }

    @Test
    void shouldUpdateEmailAddress_OfCustomer_GivenValidCustomerId() throws Exception {
        CustomerUpdateDTO customerUpdate = CustomerUpdateDTO.builder().emailAddress("updated@email.com").build();

        CustomerResponseDTO updatedResponse = CustomerResponseDTO.builder()
                                                                 .customerID(testCustomer.getCustomerId().toString())
                                                                 .firstName(testCustomer.getFirstName())
                                                                 .lastName(testCustomer.getLastName())
                                                                 .emailAddress("updated@email.com")
                                                                 .build();

        when(customerService.updateCustomer(anyString(), any(CustomerUpdateDTO.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/customers/update/{customerId}", testCustomer.getCustomerId())
                       .with(jwt().jwt(builder -> builder.subject("test-user"))
                                  .authorities(new SimpleGrantedAuthority("SCOPE_ROLE_CLIENT")))
                       .contentType(MediaType.APPLICATION_JSON)
                       //Jackson Object Mapper to Json String
                       .content(objectMapper.writeValueAsString(customerUpdate)))
               .andDo(print()).andExpect(status().isOk())
               .andExpect(jsonPath("$.emailAddress").value("updated@email.com"));

    }

    @Test
    void shouldReturnCustomerCardDetails_WhenValidIdProvided() throws Exception {
        UUID customerTestId = UUID.randomUUID();

        CardResponseV1DTO card = CardResponseV1DTO.builder()
                                                  .accountId(UUID.randomUUID().toString())
                                                  .cardNumber("4444333322221111")
                                                  .build();

        CustomerDetailsV1DTO customerDetails = CustomerDetailsV1DTO.builder()
                                                                   .firstName(testCustomer.getFirstName())
                                                                   .lastName(testCustomer.getLastName())
                                                                   .email(testCustomer.getEmailAddress())
                                                                   .cards(List.of(card))
                                                                   .build();

        when(customerService.getCustomerCardsById(anyString())).thenReturn(customerDetails);


        mockMvc.perform(get("/api/customers/{customerTestId}/details", customerTestId)
                       .with(jwt().jwt(builder -> builder.subject("test-user"))
                                  .authorities(new SimpleGrantedAuthority("SCOPE_ROLE_CLIENT"))))
               .andExpect(status().isOk())
               .andDo(print())
               .andExpect(jsonPath("$.firstName", is(testCustomer.getFirstName())))
               .andExpect(jsonPath("$.lastName", is(testCustomer.getLastName())))
               .andExpect(jsonPath("$.cards", hasSize(1)));
    }

}
