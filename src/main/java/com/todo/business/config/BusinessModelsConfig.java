package com.todo.business.config;

import com.todo.business.model.implementation.Characteristic;
import com.todo.business.model.implementation.Filter;
import com.todo.business.model.implementation.PatchElement;
import com.todo.business.model.implementation.TaskDTO;
import com.todo.business.model.interfaces.ITaskDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class BusinessModelsConfig {

    @Bean
    @Scope("prototype")
    public ITaskDTO taskDTO() {
        return new TaskDTO();
    }

    @Bean
    @Scope("prototype")
    public Filter filter() {
        return new Filter();
    }

    @Bean
    @Scope("prototype")
    public Characteristic characteristic() {
        return new Characteristic();
    }
    @Bean
    @Scope("prototype")
    public PatchElement patchElement() {
        return new PatchElement();
    }

}
