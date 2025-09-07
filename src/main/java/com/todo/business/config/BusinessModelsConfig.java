package com.todo.business.config;

import com.todo.business.model.implementation.Filter;
import com.todo.business.model.implementation.Task;
import com.todo.business.model.interfaces.ITask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class BusinessModelsConfig {

    @Bean
    @Scope("prototype")
    public ITask task() {
        return new Task();
    }

    @Bean
    @Scope("prototype")
    public Filter filter() {
        return new Filter();
    }
}
