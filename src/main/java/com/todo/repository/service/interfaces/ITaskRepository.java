package com.todo.repository.service.interfaces;

import com.todo.business.model.interfaces.ITask;
import com.todo.repository.DTO.TaskDTO;

import java.util.List;


public interface ITaskRepository {

    TaskDTO createTask(ITask task);

    List<TaskDTO> getTasksByUser(String userId, boolean includeDeleted);
}
