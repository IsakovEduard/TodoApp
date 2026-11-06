package com.todo.business.service.implementation;

import com.todo.business.constant.TaskConstants;
import com.todo.business.model.interfaces.ITaskDTO;
import com.todo.business.service.interfaces.IGetTaskByIdApplicationService;
import com.todo.business.service.interfaces.IGetUserFromContextService;
import com.todo.exception.ResourceNotFoundException;
import com.todo.repository.entity.Task;
import com.todo.repository.entity.User;
import com.todo.repository.mapper.interfaces.IMapTaskToTaskDTO;
import com.todo.repository.service.interfaces.ITaskJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetTaskByIdApplicationService implements IGetTaskByIdApplicationService {
    private static final Logger logger = LoggerFactory.getLogger(GetTaskByIdApplicationService.class);

    private final ITaskJpaRepository jpaRepository;
    private final IMapTaskToTaskDTO mapper;
    private final IGetUserFromContextService getUserFromContextService;

    public GetTaskByIdApplicationService(ITaskJpaRepository jpaRepository,
                                         IMapTaskToTaskDTO mapper,
                                         IGetUserFromContextService getUserFromContextService) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
        this.getUserFromContextService = getUserFromContextService;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            cacheNames = TaskConstants.CACHE_TASKS,
            key = "T(com.todo.business.cache.TaskCacheKeys).forCurrentUser(#id)",
            unless = "#result == null")
    public ITaskDTO execute(String id) {
        User user = getUserFromContextService.getUserFromContext();
        logger.info("Loading task id={} for userId={}", id, user.getId());
        Task task = jpaRepository.findByUserIdAndId(user.getId(), Long.valueOf(id))
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        if (TaskConstants.ACTIVATION_DELETED.equals(task.getActivationStatus())) {
            throw new ResourceNotFoundException("Task is deleted!");
        }
        return mapper.reverseMap(task);
    }
}
