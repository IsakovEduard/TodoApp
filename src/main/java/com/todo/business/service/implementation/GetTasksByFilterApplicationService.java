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
import com.todo.repository.service.interfaces.IUserJpaRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;


import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class GetTasksByFilterApplicationService implements IGetTasksByFilterApplicationService {

    @Inject
    private ITaskRepository taskRepository;
    @Inject
    private IGetUserServiceFromContextService getUserServiceFromContextService;
    @Inject
    private IMapTaskToTaskDTO mapper;
    @Inject
    private ITaskFilterDomainService taskFilterDomainService;


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
