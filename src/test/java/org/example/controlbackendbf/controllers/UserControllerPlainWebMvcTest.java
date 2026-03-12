package org.example.controlbackendbf.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.controlbackendbf.api.model.CreateUserRequest;
import org.example.controlbackendbf.api.model.User;
import org.example.controlbackendbf.controller.UserControllerPlain;
import org.example.controlbackendbf.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserControllerPlain.class)
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
public class UserControllerPlainWebMvcTest {

        @Autowired
        MockMvc mockMvc;

        @Autowired
        ObjectMapper objectMapper;

    @Autowired
    RequestMappingHandlerMapping mapping;


    @MockitoBean
        UserService userService;


    @Test
    void printMappings() {
        mapping.getHandlerMethods().forEach((info, method) ->
                System.out.println(info + " -> " + method)
        );
    }

        @Test
        void listUsers_shouldReturn200_andBody() throws Exception {
            when(userService.findAll()).thenReturn(List.of(
                    new User().id(1L).name("a"),
                    new User().id(2L).name("b")
            ));
                mockMvc.perform(get("/api/users"))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.length()").value(2))
                        .andExpect(jsonPath("$[0].id").value(1))
                        .andExpect(jsonPath("$[0].name").value("a"));

                verify(userService).findAll();
                verifyNoMoreInteractions(userService);
        }

    @Test
    void getUserById_shouldReturn200_andBody() throws Exception {
        when(userService.findById(99L)).thenReturn(new User().id(99L).name("x"));

        mockMvc.perform(get("/api/user/{id}", 99))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(99))
                .andExpect(jsonPath("$.name").value("x"));

        verify(userService).findById(99L);
        verifyNoMoreInteractions(userService);
    }

    @Test
    void createUser_shouldReturn201_andLocationHeader() throws Exception {
        CreateUserRequest req = new CreateUserRequest().name("testuser").email("email@aol.com");
        when(userService.create(any(CreateUserRequest.class)))
                .thenReturn(new User().id(123L).name("testuser"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                // Achtung: dein Controller setzt Location aktuell auf "/users/{id}" (ohne /api)
                .andExpect(header().string(HttpHeaders.LOCATION, "/users/123"))
                .andExpect(jsonPath("$.id").value(123))
                .andExpect(jsonPath("$.name").value("testuser"));

        verify(userService).create(any(CreateUserRequest.class));
    }

    @Test
    void deleteUserById_shouldReturn200() throws Exception {
        doNothing().when(userService).deleteById(5L);

        mockMvc.perform(delete("/api/user/{id}", 5))
                .andExpect(status().isOk());

        verify(userService).deleteById(5L);
    }

    @Test
    void updateUserById_shouldReturn200_andBody() throws Exception {
        CreateUserRequest req = new CreateUserRequest().name("newName").email("email@aol.com");
        when(userService.updateUserById(eq(7L), any(CreateUserRequest.class)))
                .thenReturn(new User().id(7L).name("newName"));

        mockMvc.perform(put("/api/user/{id}", 7)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.name").value("newName"));

        verify(userService).updateUserById(eq(7L), any(CreateUserRequest.class));
    }

}
