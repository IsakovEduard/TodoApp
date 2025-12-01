package com.todo.resource.delegate;

import com.todo.business.model.implementation.Filter;
import com.todo.business.model.interfaces.ITaskDTO;
import com.todo.business.service.interfaces.IGetTasksByFilterApplicationService;
import com.todo.controller.api.model.V1Task;
import com.todo.resource.mapper.interfaces.IMapITaskToV1Task;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;

import javax.inject.Inject;
import java.util.List;

public class GetTasksByFilterDelegate {

    private static final Logger logger = LoggerFactory.getLogger(GetTasksByFilterDelegate.class);

    @Inject
    private ObjectProvider<Filter> filterObjectProvider;
    @Inject
    private IGetTasksByFilterApplicationService getTasksByFilterApplicationService;
    @Inject
    private IMapITaskToV1Task outputMapper;

    public List<V1Task> execute(String taskId, String status, String urgency) {

        logger.info("In GetTasksByFilterDelegate with input: filter values: taskId={},status={},urgency{}", taskId, status, urgency);
        Filter filter = null;
        boolean isInputFilterEmpty = StringUtils.isEmpty(taskId) && StringUtils.isEmpty(status) && StringUtils.isEmpty(urgency);
        logger.info("isInputFilterEmpty: {}", isInputFilterEmpty);
        if (!isInputFilterEmpty) {
            filter = mapFilter(taskId, status, urgency);
        }
        List<ITaskDTO> tasks = getTasksByFilterApplicationService.execute(filter);
        logger.info("tasks result before output mapper: {}", tasks);
        List<V1Task> result = outputMapper.map(tasks);
        logger.info("tasks result after output mapper: {}", result);
        return result;
    }

    private Filter mapFilter(String taskId, String status, String urgency) {

        Filter filter = filterObjectProvider.getObject();
        filter.setTaskId(taskId);
        filter.setStatus(status);
        filter.setUrgency(urgency);
        return filter;
    }

}
