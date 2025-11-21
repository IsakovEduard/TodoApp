package com.todo.business.service.implementation;

import com.todo.business.domain.interfaces.ITaskFilterDomainService;
import com.todo.business.model.implementation.Filter;
import com.todo.business.model.interfaces.ITask;
import com.todo.business.service.interfaces.IGetTasksByFilterApplicationService;
import com.todo.repository.DTO.TaskDTO;
import com.todo.repository.mapper.interfaces.IMapTaskToTaskDTO;
import com.todo.repository.service.interfaces.ITaskRepository;
import org.springframework.util.CollectionUtils;


import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class GetTasksByFilterApplicationService implements IGetTasksByFilterApplicationService {

    @Inject
    private ITaskRepository taskRepository;
    @Inject
    private IMapTaskToTaskDTO mapper;
    @Inject
    private ITaskFilterDomainService taskFilterDomainService;

    @Override
    public List<ITask> execute(String userId, Filter filter) {

        List<TaskDTO> taskDTOs = taskRepository.getTasksByUser(userId, false);
        if (!CollectionUtils.isEmpty(taskDTOs) && filter != null) {
            taskDTOs = taskFilterDomainService.applyFilter(taskDTOs, filter);
        }
        if (CollectionUtils.isEmpty(taskDTOs)) {
            return new ArrayList<>();
        }
        return taskDTOs.stream().map(task -> mapper.reverseMap(task)).toList();
    }
}
