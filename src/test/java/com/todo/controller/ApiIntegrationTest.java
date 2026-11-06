package com.todo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todo.security.jwt.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:api-errors;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class ApiIntegrationTest {

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
    void createTask_ShouldReturn400_WhenUrgencyIsMissing() throws Exception {
        String token = loginAs(registerUser());

        mockMvc.perform(post("/task-management").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"t\",\"description\":\"d\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTask_ShouldReturn400_WhenUrgencyIsNotAllowed() throws Exception {
        String token = loginAs(registerUser());

        mockMvc.perform(post("/task-management").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"t\",\"description\":\"d\",\"urgency\":\"URGENT\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTask_ShouldReturn400_WhenBodyIsMalformed() throws Exception {
        String token = loginAs(registerUser());

        mockMvc.perform(post("/task-management").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{not json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTask_ShouldReturn201_WhenDescriptionIsMissing() throws Exception {
        String token = loginAs(registerUser());

        String taskId = mockMvc.perform(post("/task-management").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"No details\",\"urgency\":\"LOW\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        mockMvc.perform(get("/task-management/" + taskId).header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("No details"));
    }

    @Test
    void updateTask_ShouldClearDueDate_WhenValueIsEmpty() throws Exception {
        String token = loginAs(registerUser());
        String taskId = createTask(token);

        mockMvc.perform(put("/task-management").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"taskId\":\"" + taskId + "\",\"characteristics\":[{\"name\":\"DUE_DATE\",\"value\":\"\"}]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].dueDate").value(nullValue()));

        mockMvc.perform(get("/task-management/" + taskId).header("Authorization", bearer(token)))
                .andExpect(jsonPath("$.dueDate").value(nullValue()));
    }

    @ParameterizedTest
    @CsvSource({"STATUS,BANANA", "URGENCY,URGENT", "DUE_DATE,tomorrow"})
    void updateTask_ShouldReturn400AndKeepTaskIntact_WhenValueIsNotAllowed(String name, String value) throws Exception {
        String token = loginAs(registerUser());
        String taskId = createTask(token);

        mockMvc.perform(put("/task-management").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"taskId\":\"" + taskId + "\",\"characteristics\":[{\"name\":\"" + name + "\",\"value\":\"" + value + "\"}]}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/task-management").header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$[0].urgency").value("LOW"));
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
    void request_ShouldBeRejectedWith401_WhenTokenUserNoLongerExists() throws Exception {
        String tokenOfUnknownUser = jwtUtil.generateToken("ghost-" + UUID.randomUUID());

        mockMvc.perform(get("/task-management").header("Authorization", bearer(tokenOfUnknownUser)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void request_ShouldReturn401_WhenTokenIsMissing() throws Exception {
        mockMvc.perform(get("/task-management")).andExpect(status().isUnauthorized());
    }

    @Test
    void request_ShouldReturn401_WhenTokenIsInvalid() throws Exception {
        mockMvc.perform(get("/task-management").header("Authorization", bearer("not-a-valid-token")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void unknownPath_ShouldReturn404_WhenAuthenticated() throws Exception {
        String token = loginAs(registerUser());

        mockMvc.perform(get("/does-not-exist").header("Authorization", bearer(token)))
                .andExpect(status().isNotFound());
    }

    @Test
    void me_ShouldReturnUsername_WhenAuthenticated() throws Exception {
        String username = registerUser();
        String token = loginAs(username);

        mockMvc.perform(get("/auth/me").header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username));
    }

    @Test
    void me_ShouldReturn401_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/auth/me")).andExpect(status().isUnauthorized());
    }

    @Test
    void register_ShouldReturn400_WhenUsernameIsOnlyWhitespace() throws Exception {
        mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(credentials("   ", PASSWORD)))
                .andExpect(status().isBadRequest());
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
