package com.todo.resource.config;


import com.todo.resource.delegate.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DelegateConfig {

    @Bean
    public CreateTaskDelegate addTaskDelegate() {
        return new CreateTaskDelegate();
    }
    @Bean
    public GetTasksByFilterDelegate getTasksByFilterDelegate() {
        return new GetTasksByFilterDelegate();
    }
    @Bean
    public DeleteTasksByIdsDelegate deleteTasksByIdsDelegate() {
        return new DeleteTasksByIdsDelegate();
    }
    @Bean
    public UpdateTaskCharacteristicsDelegate updateTaskCharacteristicsDelegate() {
        return new UpdateTaskCharacteristicsDelegate();
    }
    @Bean
    public GetTaskByIdDelegate getTaskByIdDelegate() {
        return new GetTaskByIdDelegate();
    }
}
