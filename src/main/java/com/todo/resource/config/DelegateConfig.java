package com.todo.resource.config;


import com.todo.resource.delegate.CreateTaskDelegate;
import com.todo.resource.delegate.DeleteTasksByIdsDelegate;
import com.todo.resource.delegate.GetTasksByFilterDelegate;
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
}
