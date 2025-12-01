package com.todo.business.model.interfaces;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.todo.business.model.implementation.TaskDTO;
import com.todo.repository.entity.User;

import java.io.Serializable;
import java.time.LocalDateTime;

@JsonDeserialize(as = TaskDTO.class)
public interface ITaskDTO extends Serializable {


    Long getId();

    void setId(Long id);

    User getUser();
    void setUser(User user);

    String getTitle();

    void setTitle(String title);

    String getDescription();

    void setDescription(String description);

    LocalDateTime getCreatedAt();

    void setCreatedAt(LocalDateTime createdAt);


    LocalDateTime getDueDate();

    void setDueDate(LocalDateTime dueDate);

    String getUrgency();

    void setUrgency(String urgency);

    String getStatus();

    void setStatus(String status);

    String getActivationStatus();

    void setActivationStatus(String activationStatus);
}
