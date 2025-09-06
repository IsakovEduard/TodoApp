package com.todo.resource.config;


import com.todo.resource.delegate.CreateTaskDelegate;
import com.todo.resource.delegate.GetAllTasksDelegate;
import com.todo.resource.delegate.HelloDelegate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DelegateConfig {

    @Bean
    public HelloDelegate helloDelegate() {
        return new HelloDelegate();
    }
    @Bean
    public CreateTaskDelegate addTaskDelegate() {
        return new CreateTaskDelegate();
    }
    @Bean
    public GetAllTasksDelegate getAllTasksDelegate() {
        return new GetAllTasksDelegate();
    }
}
