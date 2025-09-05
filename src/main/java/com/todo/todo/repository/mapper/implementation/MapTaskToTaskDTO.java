package com.todo.todo.repository.mapper.implementation;

import com.todo.todo.business.model.interfaces.ITask;
import com.todo.todo.repository.DTO.TaskDTO;
import com.todo.todo.repository.mapper.interfaces.IMapTaskToTaskDTO;
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
        taskDTO.setDescription(task.getDescription());
        taskDTO.setTitle(task.getTitle());
        taskDTO.setCreatedAt(LocalDateTime.now());
        return taskDTO;
    }

    @Override
    public ITask reverseMap(TaskDTO taskDTO) {
        ITask task = taskProvider.getObject();
        task.setId(taskDTO.getId());
        task.setDescription(taskDTO.getDescription());
        task.setTitle(taskDTO.getTitle());
        task.setCreatedAt(taskDTO.getCreatedAt());
        return task;
    }
}
