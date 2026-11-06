package com.todo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "app.jwt.secret=test-only-secret-key-with-at-least-32-chars")
class TodoApplicationTests {

	@Test
	void contextLoads() {
	}

}
