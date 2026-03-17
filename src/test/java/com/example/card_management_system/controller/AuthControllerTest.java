package com.example.card_management_system.controller;

import com.example.card_management_system.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @Test
    @WithMockUser(username = "test-user", roles = {"USER"})
    void shouldReturnToken_WhenBasicAuthIsValid() throws Exception {
        String mockToken = "eyJhbGci0iJIUzI1NiJ9.fake.token";

        when(jwtService.generateToken(any(Authentication.class))).thenReturn(mockToken);

        mockMvc.perform(post("/api/auth/token")
                       .with(csrf())
                       .contentType(MediaType.APPLICATION_JSON))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.access_token").value(mockToken))
               .andExpect(jsonPath("$.token_type").value("Bearer"))
               .andExpect(jsonPath("$.expires_in").value(36000));
    }
}