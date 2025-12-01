package com.todo.business.service.interfaces;

import com.todo.business.model.implementation.Filter;
import com.todo.business.model.interfaces.ITaskDTO;


import java.util.List;

public interface IGetTasksByFilterApplicationService {
    List<ITaskDTO> execute(Filter filter);
}
