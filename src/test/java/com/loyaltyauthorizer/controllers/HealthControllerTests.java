package com.loyaltyauthorizer.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.loyaltyauthorizer.types.HealthCheck;

@WebMvcTest(controllers = HealthController.class)
public class HealthControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void checkReturn200() throws Exception {
        MvcResult mvcResult = mockMvc
            .perform(MockMvcRequestBuilders
            .get("/api/v1/authorizer/health-check"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

        String stringContent = mvcResult.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        HealthCheck healthCheck = mapper.readValue(stringContent, HealthCheck.class);
        String serviceName = "loyalty-authorizer";

        assertEquals(serviceName, healthCheck.getService());
    }
    
}
