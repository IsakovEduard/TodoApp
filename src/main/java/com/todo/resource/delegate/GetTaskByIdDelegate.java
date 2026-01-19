package com.todo.resource.delegate;

import com.todo.business.model.interfaces.ITaskDTO;
import com.todo.business.service.interfaces.IGetTaskByIdApplicationService;
import com.todo.controller.api.model.V1Task;
import com.todo.resource.mapper.interfaces.IMapITaskToV1Task;

import javax.inject.Inject;

public class GetTaskByIdDelegate {

    @Inject
    private IGetTaskByIdApplicationService getTaskByIdApplicationService;

    @Inject
    private IMapITaskToV1Task outputMapper;

    public V1Task execute(String taskId) {
        ITaskDTO task = getTaskByIdApplicationService.execute(taskId);
        return outputMapper.mapSingle(task);
    }
}
