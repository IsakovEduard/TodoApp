package com.todo.repository.mapper.implementation;

import com.todo.business.model.interfaces.ITask;
import com.todo.repository.DTO.TaskDTO;
import com.todo.repository.mapper.interfaces.IMapTaskToTaskDTO;
import org.springframework.beans.factory.ObjectProvider;

import javax.inject.Inject;
import java.time.LocalDateTime;

public class MapTaskToTaskDTO implements IMapTaskToTaskDTO {

    @Inject
    private ObjectProvider<TaskDTO> taskDtoProvider;
    @Inject
    private ObjectProvider<ITask> taskProvider;
    @Override
    public TaskDTO map(ITask task) {
        TaskDTO taskDTO = taskDtoProvider.getObject();
        taskDTO.setId(task.getId());
        taskDTO.setUserId(task.getUserId());
        taskDTO.setTitle(task.getTitle());
        taskDTO.setDescription(task.getDescription());
        taskDTO.setDueDate(task.getDueDate());
        taskDTO.setUrgency(task.getUrgency());
        taskDTO.setCreatedAt(task.getCreatedAt());
        taskDTO.setStatus(task.getStatus());
        taskDTO.setActivationStatus(task.getActivationStatus());

        return taskDTO;
    }

    @Override
    public ITask reverseMap(TaskDTO taskDTO) {
        ITask task = taskProvider.getObject();
        task.setId(taskDTO.getId());
        task.setUserId(taskDTO.getUserId());
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setDueDate(taskDTO.getDueDate());
        task.setUrgency(taskDTO.getUrgency());
        task.setCreatedAt(taskDTO.getCreatedAt());
        task.setStatus(taskDTO.getStatus());
        task.setActivationStatus(taskDTO.getActivationStatus());
        return task;
    }
}
