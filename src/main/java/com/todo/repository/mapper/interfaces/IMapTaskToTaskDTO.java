package com.todo.repository.mapper.interfaces;

import com.todo.business.model.interfaces.ITask;
import com.todo.repository.DTO.TaskDTO;

public interface IMapTaskToTaskDTO {
    TaskDTO map(ITask task);
    ITask reverseMap(TaskDTO taskDTO);
}
