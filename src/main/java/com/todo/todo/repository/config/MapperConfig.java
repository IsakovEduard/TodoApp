package com.todo.todo.repository.config;

import com.todo.todo.repository.mapper.implementation.MapTaskToTaskDTO;
import com.todo.todo.repository.mapper.interfaces.IMapTaskToTaskDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    public IMapTaskToTaskDTO mapTaskToTaskDTO() {
        return new MapTaskToTaskDTO();
    }
}
