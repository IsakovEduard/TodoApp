package com.todo.business.model.implementation;

import com.todo.business.model.interfaces.ITaskDTO;
import com.todo.repository.entity.User;

import java.time.LocalDateTime;

public class TaskDTO implements ITaskDTO {

    private Long id;
    private User user;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private String urgency;
    private LocalDateTime createdAt;
    private String status;
    private String activationStatus;


    @Override
    public Long getId() {
        return id;
    }
    @Override
    public void setId(Long id) {
        this.id = id;
    }
    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }
    @Override
    public String getTitle() {
        return title;
    }
    @Override
    public void setTitle(String title) {
        this.title = title;
    }
    @Override
    public String getDescription() {
        return description;
    }
    @Override
    public void setDescription(String description) {
        this.description = description;
    }
    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    @Override
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    @Override
    public LocalDateTime getDueDate() {
        return dueDate;
    }
    @Override
    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }
    @Override
    public String getUrgency() {
        return urgency;
    }
    @Override
    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }
    @Override
    public String getStatus() {
        return status;
    }
    @Override
    public void setStatus(String status) {
        this.status = status;
    }
    @Override
    public String getActivationStatus() {
        return activationStatus;
    }
    @Override
    public void setActivationStatus(String activationStatus) {
        this.activationStatus = activationStatus;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TaskDTO{");
        sb.append("id=").append(id);
        sb.append(", user=").append(user);
        sb.append(", title='").append(title).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", dueDate=").append(dueDate);
        sb.append(", urgency='").append(urgency).append('\'');
        sb.append(", createdAt=").append(createdAt);
        sb.append(", status='").append(status).append('\'');
        sb.append(", activationStatus='").append(activationStatus).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
