package com.todo.repository.config;

import com.todo.repository.mapper.implementation.MapTaskToTaskDTO;
import com.todo.repository.mapper.interfaces.IMapTaskToTaskDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryMapperConfig {

    @Bean
    public IMapTaskToTaskDTO mapTaskToTaskDTO() {
        return new MapTaskToTaskDTO();
    }
}
