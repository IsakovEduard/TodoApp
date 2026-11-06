package com.todo.business.domain.implementation;

import com.todo.business.domain.interfaces.ITaskFilterDomainService;
import com.todo.business.model.implementation.Filter;
import com.todo.repository.entity.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskFilterDomainService implements ITaskFilterDomainService {

    private static final Logger logger = LoggerFactory.getLogger(TaskFilterDomainService.class);

    @Override
    public List<Task> applyFilter(List<Task> tasks, Filter filter) {
        if (tasks == null || tasks.isEmpty() || filter == null) {
            logger.info("Skipping filter: no tasks or no filter");
            return tasks;
        }
        logger.info("Filtering {} tasks", tasks.size());

        return tasks.stream()
                .filter(task -> matches(filter.taskId(), task.getId().toString()))
                .filter(task -> matches(filter.status(), task.getStatus()))
                .filter(task -> matches(filter.urgency(), task.getUrgency()))
                .toList();
    }

    private boolean matches(String filterValue, String taskValue) {
        return filterValue == null || filterValue.isEmpty() || taskValue.equalsIgnoreCase(filterValue);
    }
}
