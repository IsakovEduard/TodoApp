package com.todo.todo.config;


import com.todo.todo.delegate.AddTaskDelegate;
import com.todo.todo.delegate.GetAllTasksDelegate;
import com.todo.todo.delegate.HelloDelegate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DelegateConfig {

    @Bean
    public HelloDelegate helloDelegate() {
        return new HelloDelegate();
    }
    @Bean
    public AddTaskDelegate addTaskDelegate() {
        return new AddTaskDelegate();
    }
    @Bean
    public GetAllTasksDelegate getAllTasksDelegate() {
        return new GetAllTasksDelegate();
    }
}
