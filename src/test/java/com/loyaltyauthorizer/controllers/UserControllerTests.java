package com.loyaltyauthorizer.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.loyaltyauthorizer.models.User;
import com.loyaltyauthorizer.repositories.UserRepository;
import com.loyaltyauthorizer.repositories.UserSessionRepository;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTests {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserSessionRepository userSessionRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createReturn201() throws Exception {
        User user = new User();
        user.setEmail("test.user@example.com");
        user.setPassword("secret123");

        when(userRepository.save(any(User.class))).thenReturn(user);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        mockMvc
            .perform(MockMvcRequestBuilders
            .post("/api/v1/authorizer/users")
            .contentType("application/json")
            .content(mapper.writeValueAsString(user)))
            .andExpect(MockMvcResultMatchers.status().isCreated());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void disableReturn404() throws Exception {
        when(userRepository.findById(anyString())).thenReturn(Optional.<User>empty());

        mockMvc
            .perform(MockMvcRequestBuilders
            .delete("/api/v1/authorizer/users/disable/63267c6da5fbc9719d2db974"))
            .andExpect(MockMvcResultMatchers.status().isNotFound());

        verify(userRepository, times(1)).findById(anyString());
        verify(userSessionRepository, times(0)).destroySession(anyString());
    }

    @Test
    void disableReturn204() throws Exception {
        when(userRepository.findById(anyString())).thenReturn(Optional.of(new User()));
        doNothing().when(spy(userSessionRepository)).destroySession(anyString());

        mockMvc
            .perform(MockMvcRequestBuilders
            .delete("/api/v1/authorizer/users/disable/63267c6da5fbc9719d2db974"))
            .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(userRepository, times(1)).findById(anyString());
        verify(userSessionRepository, times(1)).destroySession(anyString());
    }
    
}
