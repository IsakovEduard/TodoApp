package com.todo.todo.repository.DTO;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class TaskDTO {


    @Id
    @Column(name ="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name ="task_description")
    private String description;
    @Column(name ="title")
    private String title;
    @Column(name ="created_At")
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    // No setter for ID
    //    public void setId(Long id) {
    //        this.id = id;
    //    }

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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Task{");
        sb.append("id=").append(id);
        sb.append(", description='").append(description).append('\'');
        sb.append(", title='").append(title).append('\'');
        sb.append(", createdAt=").append(createdAt);
        sb.append('}');
        return sb.toString();
    }

}
