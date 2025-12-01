package com.todo.resource.mapper.interfaces;

import com.todo.business.model.interfaces.ITaskDTO;
import com.todo.controller.api.model.V1Task;

import java.util.List;

public interface IMapITaskToV1Task {
    List<V1Task> map(List<ITaskDTO> tasks);
    V1Task mapSingle(ITaskDTO task);
}
