package com.todo.repository.mapper.interfaces;

import com.todo.business.model.interfaces.ITaskDTO;
import com.todo.repository.entity.Task;

public interface IMapTaskToTaskDTO {
    Task map(ITaskDTO task);
    ITaskDTO reverseMap(Task task);
}
