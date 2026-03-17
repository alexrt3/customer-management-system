package com.example.card_management_system.controller;

import com.example.card_management_system.dto.CreateCustomerRequestDTO;
import com.example.card_management_system.dto.CustomerUpdateDTO;
import com.example.card_management_system.entity.Customer;
import com.example.card_management_system.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CustomerControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;


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
                               .phoneNumber("123123123121332")
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
        //make sure Customer table is empty
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
                                                                           .state("IL")
                                                                           .zipCode("60606")
                                                                           .build();

        mockMvc.perform(post("/api/customers/create")
                       .with(jwt().jwt(builder -> builder.subject("test-user"))
                                  .authorities(new SimpleGrantedAuthority("SCOPE_ROLE_CLIENT")))
                       .contentType(MediaType.APPLICATION_JSON)
                       //Jackson Object Mapper to Json String
                       .content(objectMapper.writeValueAsString(customerRequest)))
               .andDo(print()).andExpect(status().isCreated());

        assertThat(customerRepository.count()).isEqualTo(1);
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
        mockMvc.perform(get("/api/customers/find/{customerId}", testCustomer.getCustomerId())
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

        mockMvc.perform(put("/api/customers/update/{customerId}", testCustomer.getCustomerId())
                       .with(jwt().jwt(builder -> builder.subject("test-user"))
                                  .authorities(new SimpleGrantedAuthority("SCOPE_ROLE_CLIENT")))
                       .contentType(MediaType.APPLICATION_JSON)
                       //Jackson Object Mapper to Json String
                       .content(objectMapper.writeValueAsString(customerUpdate)))
               .andDo(print()).andExpect(status().isOk());

        Customer updatedCustomer = customerRepository.findById(testCustomer.getCustomerId()).orElse(null);

        assertThat(updatedCustomer).isNotNull();
        assertThat(updatedCustomer.getEmailAddress()).isEqualTo(customerUpdate.getEmailAddress());
    }
}
