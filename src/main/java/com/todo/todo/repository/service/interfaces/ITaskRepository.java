package com.todo.todo.repository.service.interfaces;

import com.todo.todo.business.model.interfaces.ITask;
import com.todo.todo.repository.DTO.TaskDTO;


public interface ITaskRepository {

    TaskDTO createTask(ITask task);
}
