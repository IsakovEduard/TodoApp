package com.todo.business.service.implementation;

import com.todo.business.constant.TaskConstants;
import com.todo.business.model.interfaces.ITaskDTO;
import com.todo.business.service.interfaces.IGetTaskByIdApplicationService;
import com.todo.repository.entity.Task;
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

    public GetTaskByIdApplicationService(ITaskJpaRepository jpaRepository, IMapTaskToTaskDTO mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            cacheNames = TaskConstants.CACHE_TASKS,
            key = "#id.toString()",
            unless = "#result == null")
    public ITaskDTO execute(String id) {
        logger.info("Loading task id={}", id);
        Task task = jpaRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new RuntimeException("Task not found"));
        if (TaskConstants.ACTIVATION_DELETED.equals(task.getActivationStatus())) {
            throw new RuntimeException("Task is deleted!");
        }
        return mapper.reverseMap(task);
    }
}
