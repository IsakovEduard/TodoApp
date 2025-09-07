package com.todo.business.config;

import com.todo.business.service.implementation.CreateTaskApplicationService;
import com.todo.business.service.implementation.GetTasksByFilterApplicationService;
import com.todo.business.service.interfaces.ICreateTaskApplicationService;
import com.todo.business.service.interfaces.IGetTasksByFilterApplicationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationServiceConfig {

    @Bean
    public ICreateTaskApplicationService createTaskApplicationService() {
        return new CreateTaskApplicationService();
    }

    @Bean
    public IGetTasksByFilterApplicationService getTasksByFilterApplicationService() {
        return new GetTasksByFilterApplicationService();
    }
}
