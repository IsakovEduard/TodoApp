package com.todo.business.service.interfaces;

import com.todo.business.model.implementation.Filter;
import com.todo.business.model.interfaces.ITask;


import java.util.List;

public interface IGetTasksByFilterApplicationService {
    List<ITask> execute(String userId, Filter filter);
}
