package com.todo.resource.delegate;

import com.todo.business.model.implementation.Filter;
import com.todo.business.model.interfaces.ITask;
import com.todo.business.service.interfaces.IGetTasksByFilterApplicationService;
import com.todo.model.V1SearchFilter;
import com.todo.model.V1Task;
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

    public List<V1Task> execute(String userId, V1SearchFilter v1SearchFilter) {
        // TODO: Validate input
        logger.info("In GetTasksByFilterDelegate with input: userId: {}, filter: {}", userId, v1SearchFilter);
        Filter filter = null;
        boolean isInputFilterEmpty = isFilterEmpty(v1SearchFilter);
        logger.info("isInputFilterEmpty: {}", isInputFilterEmpty);
        if (!isInputFilterEmpty) {
            filter = mapFilter(v1SearchFilter);
        }
        List<ITask> tasks = getTasksByFilterApplicationService.execute(userId, filter);
        logger.info("tasks result before output mapper: {}", tasks);
        List<V1Task> result = outputMapper.map(tasks);
        logger.info("tasks result after output mapper: {}", result);
        return result;
    }

    private boolean isFilterEmpty(V1SearchFilter filter) {
        return  (filter == null || (
                filter.getStatus() == null && StringUtils.isEmpty(filter.getTaskId()) && filter.getUrgency() == null)
        );
    }

    private Filter mapFilter(V1SearchFilter v1SearchFilter) {

        Filter filter = filterObjectProvider.getObject();
        filter.setTaskId(v1SearchFilter.getTaskId());
        if (v1SearchFilter.getStatus() != null) {
            filter.setStatus(v1SearchFilter.getStatus().getValue());
        }
        if (v1SearchFilter.getUrgency() != null) {
            filter.setUrgency(v1SearchFilter.getUrgency().getValue());
        }
        return filter;
    }
}
