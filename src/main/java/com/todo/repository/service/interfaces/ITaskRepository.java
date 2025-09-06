package com.todo.repository.service.interfaces;

import com.todo.business.model.interfaces.ITask;
import com.todo.repository.DTO.TaskDTO;


public interface ITaskRepository {

    TaskDTO createTask(ITask task);
}
