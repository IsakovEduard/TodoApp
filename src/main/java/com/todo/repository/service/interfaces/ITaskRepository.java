package com.todo.repository.service.interfaces;

import com.todo.business.model.interfaces.ITaskDTO;
import com.todo.repository.entity.Task;

import java.util.List;


public interface ITaskRepository {

    Task createTask(ITaskDTO task);

    List<Task> getTasksByUser(Long userId, boolean includeDeleted);

    Task getTaskByUserIdAndTaskId(Long userId, Long taskId);
}
