package com.todo.resource.delegate;

import com.todo.business.model.interfaces.ITaskDTO;
import com.todo.business.service.interfaces.IGetTaskByIdApplicationService;
import com.todo.controller.api.model.V1Task;
import com.todo.resource.mapper.interfaces.IMapITaskToV1Task;
import org.springframework.stereotype.Component;

@Component
public class GetTaskByIdDelegate {

    private final IGetTaskByIdApplicationService getTaskByIdApplicationService;
    private final IMapITaskToV1Task outputMapper;

    public GetTaskByIdDelegate(IGetTaskByIdApplicationService getTaskByIdApplicationService,
                               IMapITaskToV1Task outputMapper) {
        this.getTaskByIdApplicationService = getTaskByIdApplicationService;
        this.outputMapper = outputMapper;
    }

    public V1Task execute(String taskId) {
        ITaskDTO task = getTaskByIdApplicationService.execute(taskId);
        return outputMapper.mapSingle(task);
    }
}
