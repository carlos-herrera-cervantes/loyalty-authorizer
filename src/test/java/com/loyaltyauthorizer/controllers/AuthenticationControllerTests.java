package com.loyaltyauthorizer.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loyaltyauthorizer.models.User;
import com.loyaltyauthorizer.repositories.UserRepository;
import com.loyaltyauthorizer.repositories.UserSessionRepository;
import com.loyaltyauthorizer.services.TokenManagerService;
import com.loyaltyauthorizer.types.Credentials;
import com.loyaltyauthorizer.types.MessageResponse;

@WebMvcTest(controllers = AuthenticationController.class)
public class AuthenticationControllerTests {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private TokenManagerService tokenManagerService;

    @MockBean
    private UserSessionRepository userSessionRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void signInReturn404() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(null);

        Credentials credentials = new Credentials();
        credentials.setEmail("test.user@example.com");
        credentials.setPassword("secret123");

        ObjectMapper mapper = new ObjectMapper();

        mockMvc
            .perform(MockMvcRequestBuilders
            .post("/api/v1/authorizer/authentication/sign-in")
            .contentType("application/json")
            .content(mapper.writeValueAsString(credentials)))
            .andExpect(MockMvcResultMatchers.status().isNotFound());

        verify(userRepository, times(1)).findByEmail(anyString());
        verify(tokenManagerService, times(0)).generateToken(any(User.class));
        verify(userSessionRepository, times(0)).setSession(anyString(), anyString());
    }

    @Test
    void signInReturn401() throws Exception {
        User user = new User();
        user.setPassword("secret123");
        user.setDeactivated(false);

        when(userRepository.findByEmail(anyString())).thenReturn(user);

        Credentials credentials = new Credentials();
        credentials.setEmail("test.user@example.com");
        credentials.setPassword("bad password");

        ObjectMapper mapper = new ObjectMapper();

        mockMvc
            .perform(MockMvcRequestBuilders
            .post("/api/v1/authorizer/authentication/sign-in")
            .contentType("application/json")
            .content(mapper.writeValueAsString(credentials)))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        verify(userRepository, times(1)).findByEmail(anyString());
        verify(tokenManagerService, times(0)).generateToken(any(User.class));
        verify(userSessionRepository, times(0)).setSession(anyString(), anyString());
    }

    @Test
    void signInReturn200() throws Exception {
        User user = new User();
        user.setPassword("secret123");
        user.setDeactivated(false);

        when(userRepository.findByEmail(anyString())).thenReturn(user);
        when(tokenManagerService.generateToken(any(User.class))).thenReturn("dummy-jwt");
        doNothing().when(spy(userSessionRepository)).setSession(anyString(), anyString());

        Credentials credentials = new Credentials();
        credentials.setEmail("test.user@example.com");
        credentials.setPassword("secret123");

        ObjectMapper mapper = new ObjectMapper();

        MvcResult mvcResult = mockMvc
            .perform(MockMvcRequestBuilders
            .post("/api/v1/authorizer/authentication/sign-in")
            .contentType("application/json")
            .content(mapper.writeValueAsString(credentials)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

        verify(userRepository, times(1)).findByEmail(anyString());
        verify(tokenManagerService, times(1)).generateToken(any(User.class));
        verify(userSessionRepository, times(1)).setSession(anyString(), anyString());

        String stringContent = mvcResult.getResponse().getContentAsString();
        MessageResponse messageResponse = mapper.readValue(stringContent, MessageResponse.class);

        assertEquals("dummy-jwt", messageResponse.getMessage());
    }

    @Test
    void signOutReturn204() throws Exception {
        doNothing().when(spy(userSessionRepository)).destroySession(anyString());

        mockMvc
            .perform(MockMvcRequestBuilders
            .post("/api/v1/authorizer/authentication/sign-out")
            .header("user-email", "test.user@example.com"))
            .andExpect(MockMvcResultMatchers.status().isNoContent())
            .andReturn();

        verify(userSessionRepository, times(1)).destroySession(anyString());
    }
    
}
