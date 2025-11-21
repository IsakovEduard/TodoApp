package com.todo.business.domain.implementation;

import com.todo.business.domain.interfaces.ITaskFilterDomainService;
import com.todo.business.model.implementation.Filter;
import com.todo.repository.DTO.TaskDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class TaskFilterDomainService implements ITaskFilterDomainService {

    private static final Logger logger = LoggerFactory.getLogger(TaskFilterDomainService.class);
    @Override
    public List<TaskDTO> applyFilter(List<TaskDTO> tasks, Filter filter) {
        if (tasks == null || tasks.isEmpty() || filter == null) {
            logger.info("In TaskFilterDomainService with empty payload");
            return tasks; // return original list if null/empty or no filter
        }
        logger.info("In TaskFilterDomainService with {} tasks before filter", tasks.size());

        String taskIdFilter = filter.getTaskId();
        String StatusFilter = filter.getStatus();
        String urgencyFilter = filter.getUrgency();

        return tasks.stream()
                .filter(task -> taskIdFilter == null || taskIdFilter.isEmpty() || task.getId().toString().equals(taskIdFilter))
                .filter(task -> StatusFilter == null || StatusFilter.isEmpty() || task.getStatus().equalsIgnoreCase(StatusFilter))
                .filter(task -> urgencyFilter == null || urgencyFilter.isEmpty() || task.getUrgency().equalsIgnoreCase(urgencyFilter))
                .collect(Collectors.toList());
    }
}
