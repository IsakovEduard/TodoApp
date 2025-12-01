package com.todo.business.config;

import com.todo.business.service.implementation.*;
import com.todo.business.service.interfaces.*;
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
    @Bean
    public IDeleteTasksByIdsApplicationService deleteTasksByIdsApplicationService() {
        return new DeleteTasksByIdsApplicationService();
    }
    @Bean
    public IUpdateTaskCharacteristicsApplicationService updateTaskCharacteristicsApplicationService() {
        return new UpdateTaskCharacteristicsApplicationService();
    }
    @Bean
    public IGetUserServiceFromContextService getUserServiceFromContextService() {
        return new GetUserServiceFromContextServiceService();
    }
}
