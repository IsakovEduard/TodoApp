package com.todo.todo.repository.config;

import com.todo.todo.repository.DTO.TaskDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class DTOConfig {

    @Bean
    @Scope("prototype")
    public TaskDTO taskDTO() {
        return new TaskDTO();
    }
}
