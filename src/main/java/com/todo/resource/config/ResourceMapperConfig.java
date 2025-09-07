package com.todo.resource.config;

import com.todo.resource.mapper.implementation.MapITaskToV1Task;
import com.todo.resource.mapper.interfaces.IMapITaskToV1Task;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResourceMapperConfig {

    @Bean
    public IMapITaskToV1Task mapITaskToV1Task() {
        return new MapITaskToV1Task();
    }
}
