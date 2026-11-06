package com.todo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todo.security.jwt.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:api-errors;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class ApiErrorResponsesTest {

    private static final String PASSWORD = "pw123456";
    private static final String NEW_TASK_JSON =
            "{\"title\":\"t\",\"description\":\"d\",\"urgency\":\"LOW\",\"dueDate\":\"2026-12-01T10:00:00\"}";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void login_ShouldReturn401_WhenPasswordIsWrong() throws Exception {
        String username = registerUser();

        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(credentials(username, "wrong-password")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_ShouldReturn401_WhenUserIsUnknown() throws Exception {
        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(credentials("user-" + UUID.randomUUID(), PASSWORD)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void register_ShouldReturn400_WhenUsernameIsBlank() throws Exception {
        mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(credentials("", PASSWORD)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_ShouldReturn400_WhenPasswordIsMissing() throws Exception {
        mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user-" + UUID.randomUUID() + "\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_ShouldReturn400_WhenCredentialsAreMissing() throws Exception {
        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTaskById_ShouldReturn404_WhenTaskDoesNotExist() throws Exception {
        String token = loginAs(registerUser());

        mockMvc.perform(get("/task-management/999999").header("Authorization", bearer(token)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTaskById_ShouldReturn404_WhenTaskBelongsToAnotherUser() throws Exception {
        String ownerToken = loginAs(registerUser());
        String otherToken = loginAs(registerUser());
        String taskId = createTask(ownerToken);

        mockMvc.perform(get("/task-management/" + taskId).header("Authorization", bearer(ownerToken)))
                .andExpect(status().isOk());
        mockMvc.perform(get("/task-management/" + taskId).header("Authorization", bearer(otherToken)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTaskById_ShouldReturn404_WhenTaskIsDeleted() throws Exception {
        String token = loginAs(registerUser());
        String taskId = createTask(token);
        deleteTask(token, taskId);

        mockMvc.perform(get("/task-management/" + taskId).header("Authorization", bearer(token)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTask_ShouldReturn404_WhenTaskIsDeleted() throws Exception {
        String token = loginAs(registerUser());
        String taskId = createTask(token);
        deleteTask(token, taskId);

        mockMvc.perform(put("/task-management").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"taskId\":\"" + taskId + "\",\"characteristics\":[{\"name\":\"title\",\"value\":\"x\"}]}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void request_ShouldBeRejectedWith403_WhenTokenUserNoLongerExists() throws Exception {
        String tokenOfUnknownUser = jwtUtil.generateToken("ghost-" + UUID.randomUUID());

        mockMvc.perform(get("/task-management").header("Authorization", bearer(tokenOfUnknownUser)))
                .andExpect(status().isForbidden());
    }

    private String registerUser() throws Exception {
        String username = "user-" + UUID.randomUUID();
        mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(credentials(username, PASSWORD)))
                .andExpect(status().isOk());
        return username;
    }

    private String loginAs(String username) throws Exception {
        String body = mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(credentials(username, PASSWORD)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body).get("token").asText();
    }

    private String createTask(String token) throws Exception {
        return mockMvc.perform(post("/task-management").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content(NEW_TASK_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
    }

    private void deleteTask(String token, String taskId) throws Exception {
        mockMvc.perform(delete("/task-management").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"taskIds\":[\"" + taskId + "\"]}"))
                .andExpect(status().isOk());
    }

    private String credentials(String username, String password) {
        return "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
