package com.todo.repository.mapper.implementation;

import com.todo.business.model.implementation.TaskDTO;
import com.todo.business.model.interfaces.ITaskDTO;
import com.todo.repository.entity.Task;
import com.todo.repository.mapper.interfaces.IMapTaskToTaskDTO;
import org.springframework.stereotype.Component;

@Component
public class MapTaskToTaskDTO implements IMapTaskToTaskDTO {

    @Override
    public Task map(ITaskDTO taskDto) {
        Task task = new Task();
        task.setId(taskDto.getId());
        task.setUser(taskDto.getUser());
        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setDueDate(taskDto.getDueDate());
        task.setUrgency(taskDto.getUrgency());
        task.setCreatedAt(taskDto.getCreatedAt());
        task.setStatus(taskDto.getStatus());
        task.setActivationStatus(taskDto.getActivationStatus());

        return task;
    }

    @Override
    public ITaskDTO reverseMap(Task task) {
        ITaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(task.getId());
        taskDTO.setUser(task.getUser());
        taskDTO.setTitle(task.getTitle());
        taskDTO.setDescription(task.getDescription());
        taskDTO.setDueDate(task.getDueDate());
        taskDTO.setUrgency(task.getUrgency());
        taskDTO.setCreatedAt(task.getCreatedAt());
        taskDTO.setStatus(task.getStatus());
        taskDTO.setActivationStatus(task.getActivationStatus());
        return taskDTO;
    }
}
