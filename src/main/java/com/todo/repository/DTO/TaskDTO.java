package com.todo.repository.DTO;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class TaskDTO {


    @Id
    @Column(name ="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private String userId;
    @Column(name ="title")
    private String title;
    @Column(name ="task_description")
    private String description;
    @Column(name ="due_date")
    private LocalDateTime dueDate;
    @Column(name ="urgency")
    private String urgency;
    @Column(name ="created_at")
    private LocalDateTime createdAt;
    @Column(name ="status")
    private String status;
    @Column(name = "activation_status")
    private String activationStatus;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getActivationStatus() {
        return activationStatus;
    }

    public void setActivationStatus(String activationStatus) {
        this.activationStatus = activationStatus;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TaskDTO{");
        sb.append("id=").append(id);
        sb.append(", userId='").append(userId).append('\'');
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
