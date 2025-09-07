package com.todo.resource.mapper.interfaces;

import com.todo.business.model.interfaces.ITask;
import com.todo.model.V1Task;

import java.util.List;

public interface IMapITaskToV1Task {
    List<V1Task> map(List<ITask> tasks);
    V1Task mapSingle(ITask task);
}
