package com.todo.todo.business.model.interfaces;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.todo.todo.business.model.implementation.Task;

import java.io.Serializable;
import java.time.LocalDateTime;

@JsonDeserialize(as = Task.class)
public interface ITask extends Serializable {


    Long getId();

    void setId(Long id);

    String getTitle();

    void setTitle(String title);

    String getDescription();

    void setDescription(String description);

    LocalDateTime getCreatedAt();

    void setCreatedAt(LocalDateTime createdAt);
}
