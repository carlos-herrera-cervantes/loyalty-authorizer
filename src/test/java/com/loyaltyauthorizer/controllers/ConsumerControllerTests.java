package com.loyaltyauthorizer.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.loyaltyauthorizer.models.Consumer;
import com.loyaltyauthorizer.repositories.ConsumerRepository;
import com.loyaltyauthorizer.repositories.UserSessionRepository;
import com.loyaltyauthorizer.services.ApiKeyService;

@WebMvcTest(controllers = ConsumerController.class)
public class ConsumerControllerTests {

    @MockBean
    private ConsumerRepository consumerRepository;

    @MockBean
    private ApiKeyService apiKeyService;

    @MockBean
    private UserSessionRepository userSessionRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void findAllReturn200() throws Exception {
        when(consumerRepository.findAll(any(Pageable.class))).thenReturn(null);

        mockMvc
            .perform(MockMvcRequestBuilders
            .get("/api/v1/authorizer/consumers"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

        verify(consumerRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void createReturn201() throws Exception {
        Consumer consumer = new Consumer();
        consumer.setName("test");

        when(apiKeyService.generateApiKey(anyInt())).thenReturn("DUMMYAPIKEY");
        when(consumerRepository.save(any(Consumer.class))).thenReturn(consumer);
        doNothing().when(spy(userSessionRepository)).setSession(anyString(), anyString());

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        mockMvc
            .perform(MockMvcRequestBuilders
            .post("/api/v1/authorizer/consumers")
            .contentType("application/json")
            .content(mapper.writeValueAsString(consumer)))
            .andExpect(MockMvcResultMatchers.status().isCreated());

        verify(apiKeyService, times(1)).generateApiKey(anyInt());
        verify(consumerRepository, times(1)).save(any(Consumer.class));
        verify(userSessionRepository, times(1)).setSession(anyString(), anyString());
    }

    @Test
    void createReturn500() throws Exception {
        Consumer consumer = new Consumer();
        consumer.setName("test");

        when(apiKeyService.generateApiKey(anyInt()))
            .thenThrow(new NoSuchAlgorithmException());

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        mockMvc
            .perform(MockMvcRequestBuilders
            .post("/api/v1/authorizer/consumers")
            .contentType("application/json")
            .content(mapper.writeValueAsString(consumer)))
            .andExpect(MockMvcResultMatchers.status().isInternalServerError());

        verify(apiKeyService, times(1)).generateApiKey(anyInt());
        verify(consumerRepository, times(0)).save(any(Consumer.class));
        verify(userSessionRepository, times(0)).setSession(anyString(), anyString());
    }

    @Test
    void deleteByIdReturn404() throws Exception {
        when(consumerRepository.findById(anyString())).thenReturn(Optional.<Consumer>empty());

        mockMvc
            .perform(MockMvcRequestBuilders
            .delete("/api/v1/authorizer/consumers/disable/63267c6da5fbc9719d2db974")
            .header("api-key", "DUMMYAPIKEY"))
            .andExpect(MockMvcResultMatchers.status().isNotFound());

        verify(consumerRepository, times(1)).findById(anyString());
        verify(consumerRepository, times(0)).save(any(Consumer.class));
        verify(userSessionRepository, times(0)).destroySession(anyString());
    }

    @Test
    void deleteByIdReturn204() throws Exception {
        when(consumerRepository.findById(anyString())).thenReturn(Optional.of(new Consumer()));
        when(consumerRepository.save(any(Consumer.class))).thenReturn(new Consumer());
        doNothing().when(spy(userSessionRepository)).destroySession(anyString());

        mockMvc
            .perform(MockMvcRequestBuilders
            .delete("/api/v1/authorizer/consumers/disable/63267c6da5fbc9719d2db974")
            .header("api-key", "DUMMYAPIKEY"))
            .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(consumerRepository, times(1)).findById(anyString());
        verify(consumerRepository, times(1)).save(any(Consumer.class));
        verify(userSessionRepository, times(1)).destroySession(anyString());
    }
    
}
