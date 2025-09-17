package com.todo.business.model.implementation;

import java.util.List;

public class PatchElement {
    private String taskId;
    private List<Characteristic> changeCharacteristics;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public List<Characteristic> getChangeCharacteristics() {
        return changeCharacteristics;
    }

    public void setChangeCharacteristics(List<Characteristic> changeCharacteristics) {
        this.changeCharacteristics = changeCharacteristics;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PatchElement{");
        sb.append("taskId='").append(taskId).append('\'');
        sb.append(", changeCharacteristics=").append(changeCharacteristics);
        sb.append('}');
        return sb.toString();
    }
}
