package com.todo.business.model.implementation;

public class Filter {
    private String taskId;
    private String status;
    private String urgency;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Filter{");
        sb.append("taskId='").append(taskId).append('\'');
        sb.append(", status='").append(status).append('\'');
        sb.append(", urgency='").append(urgency).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
