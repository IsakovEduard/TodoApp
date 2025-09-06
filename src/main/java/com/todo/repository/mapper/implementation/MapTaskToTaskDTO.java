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
        taskDTO.setTitle(task.getTitle());
        taskDTO.setDescription(task.getDescription());
        taskDTO.setDueDate(task.getDueDate());
        taskDTO.setUrgency(task.getUrgency());
        taskDTO.setCreatedAt(task.getCreatedAt());

        return taskDTO;
    }

    @Override
    public ITask reverseMap(TaskDTO taskDTO) {
        ITask task = taskProvider.getObject();
        task.setId(taskDTO.getId());
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setDueDate(taskDTO.getDueDate());
        task.setUrgency(taskDTO.getUrgency());
        task.setCreatedAt(taskDTO.getCreatedAt());

        return task;
    }
}
