package com.todo.resource.delegate;

import com.todo.business.model.implementation.Filter;
import com.todo.business.model.interfaces.ITaskDTO;
import com.todo.business.service.interfaces.IGetTasksByFilterApplicationService;
import com.todo.controller.api.model.V1Task;
import com.todo.resource.mapper.interfaces.IMapITaskToV1Task;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetTasksByFilterDelegate {

    private static final Logger logger = LoggerFactory.getLogger(GetTasksByFilterDelegate.class);

    private final IGetTasksByFilterApplicationService getTasksByFilterApplicationService;
    private final IMapITaskToV1Task outputMapper;

    public GetTasksByFilterDelegate(IGetTasksByFilterApplicationService getTasksByFilterApplicationService,
                                    IMapITaskToV1Task outputMapper) {
        this.getTasksByFilterApplicationService = getTasksByFilterApplicationService;
        this.outputMapper = outputMapper;
    }

    public List<V1Task> execute(String taskId, String status, String urgency) {
        logger.info("Getting tasks: taskId={}, status={}, urgency={}", taskId, status, urgency);
        boolean isFilterEmpty = StringUtils.isEmpty(taskId) && StringUtils.isEmpty(status) && StringUtils.isEmpty(urgency);
        Filter filter = isFilterEmpty ? null : new Filter(taskId, status, urgency);

        List<ITaskDTO> tasks = getTasksByFilterApplicationService.execute(filter);
        logger.info("Found {} tasks", tasks.size());
        return outputMapper.map(tasks);
    }
}
