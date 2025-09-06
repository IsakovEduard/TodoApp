package com.todo.business.config;

import com.todo.business.model.implementation.Task;
import com.todo.business.model.interfaces.ITask;
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
