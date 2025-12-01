package com.todo.repository.config;

import com.todo.repository.entity.Task;
import com.todo.repository.entity.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class EntityConfig {

    @Bean
    @Scope("prototype")
    public Task task() {
        return new Task();
    }
    @Bean
    @Scope("prototype")
    public User user() {
        return new User();
    }
}
