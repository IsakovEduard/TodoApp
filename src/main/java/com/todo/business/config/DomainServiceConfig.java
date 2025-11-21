package com.todo.business.config;

import com.todo.business.domain.implementation.TaskFilterDomainService;
import com.todo.business.domain.interfaces.ITaskFilterDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServiceConfig {

    @Bean
    public ITaskFilterDomainService taskFilterDomainService() {
        return new TaskFilterDomainService();
    }
}
