package com.todo.business.service.implementation;

import com.todo.business.domain.interfaces.ITaskFilterDomainService;
import com.todo.business.model.implementation.Filter;
import com.todo.business.model.interfaces.ITaskDTO;
import com.todo.business.service.interfaces.IGetTasksByFilterApplicationService;
import com.todo.business.service.interfaces.IGetUserServiceFromContextService;
import com.todo.repository.entity.Task;
import com.todo.repository.entity.User;
import com.todo.repository.mapper.interfaces.IMapTaskToTaskDTO;
import com.todo.repository.service.interfaces.ITaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class GetTasksByFilterApplicationService implements IGetTasksByFilterApplicationService {

    private final ITaskRepository taskRepository;
    private final IGetUserServiceFromContextService getUserServiceFromContextService;
    private final IMapTaskToTaskDTO mapper;
    private final ITaskFilterDomainService taskFilterDomainService;

    public GetTasksByFilterApplicationService(ITaskRepository taskRepository,
                                              IGetUserServiceFromContextService getUserServiceFromContextService,
                                              IMapTaskToTaskDTO mapper,
                                              ITaskFilterDomainService taskFilterDomainService) {
        this.taskRepository = taskRepository;
        this.getUserServiceFromContextService = getUserServiceFromContextService;
        this.mapper = mapper;
        this.taskFilterDomainService = taskFilterDomainService;
    }

    @Override
    public List<ITaskDTO> execute(Filter filter) {
        User user = getUserServiceFromContextService.getUserFromContext();
        List<Task> taskEntities = taskRepository.getTasksByUser(user.getId(), false);
        if (!CollectionUtils.isEmpty(taskEntities) && filter != null) {
            taskEntities = taskFilterDomainService.applyFilter(taskEntities, filter);
        }
        if (CollectionUtils.isEmpty(taskEntities)) {
            return new ArrayList<>();
        }
        return taskEntities.stream().map(task -> mapper.reverseMap(task)).toList();
    }
}
