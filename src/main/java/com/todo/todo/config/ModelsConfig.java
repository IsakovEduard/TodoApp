package com.todo.todo.config;

import com.todo.todo.business.model.implementation.Task;
import com.todo.todo.business.model.interfaces.ITask;
import com.todo.todo.repository.DTO.TaskDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class ModelsConfig {

    @Bean
    @Scope("prototype")
    public ITask task() {
        return new Task();
    }
}
