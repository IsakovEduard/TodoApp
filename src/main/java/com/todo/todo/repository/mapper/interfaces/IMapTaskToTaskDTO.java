package com.todo.todo.repository.mapper.interfaces;

import com.todo.todo.business.model.interfaces.ITask;
import com.todo.todo.repository.DTO.TaskDTO;

public interface IMapTaskToTaskDTO {
    TaskDTO map(ITask task);
    ITask reverseMap(TaskDTO taskDTO);
}
