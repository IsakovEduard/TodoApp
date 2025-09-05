package com.todo.todo.repository.config;

import com.todo.todo.repository.service.implementation.TaskRepository;
import com.todo.todo.repository.service.interfaces.ITaskRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {

    @Bean
    public ITaskRepository taskRepository() {
        return new TaskRepository();
    }
}
