package com.todo;

import com.todo.security.jwt.JwtProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:dev-profile")
@ActiveProfiles("dev")
class DevProfileContextTest {

    @Autowired
    private JwtProperties jwtProperties;

    @Test
    void contextLoads_ShouldProvideValidJwtSecret_WhenDevProfileActive() {
        assertTrue(jwtProperties.secret().length() >= 32);
    }
}
