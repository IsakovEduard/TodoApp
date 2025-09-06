package com.todo.business.service.interfaces;

import com.todo.business.model.interfaces.ITask;

public interface ICreateTaskApplicationService {
    ITask execute(ITask task);
}
