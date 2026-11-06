package com.todo.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:ui-access")
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class UiAccessTest {

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @ValueSource(strings = {"/login", "/register", "/tasks", "/tasks/42"})
    void uiRoute_ShouldForwardToIndexPage_WithoutToken(String path) throws Exception {
        mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("/index.html"));
    }

    @Test
    void root_ShouldServeWelcomePage_WithoutToken() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("index.html"));
    }

    @Test
    void indexPage_ShouldBeServed_WithoutToken() throws Exception {
        mockMvc.perform(get("/index.html"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("todo-ui")));
    }

    @Test
    void staticAsset_ShouldBeServed_WithoutToken() throws Exception {
        mockMvc.perform(get("/assets/app.js")).andExpect(status().isOk());
    }

    @Test
    void api_ShouldRemainProtected_WithoutToken() throws Exception {
        mockMvc.perform(get("/task-management")).andExpect(status().isUnauthorized());
    }
}
