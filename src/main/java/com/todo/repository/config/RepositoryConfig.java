package com.todo.repository.config;

import com.todo.repository.service.implementation.TaskRepository;
import com.todo.repository.service.interfaces.ITaskRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {

    @Bean
    public ITaskRepository taskRepository() {
        return new TaskRepository();
    }
}
