package com.todo.business.service.interfaces;

import com.todo.business.model.interfaces.ITaskDTO;

public interface ICreateTaskApplicationService {
    ITaskDTO execute(ITaskDTO task);
}
