package com.todo.business.service.implementation;

import com.todo.business.model.interfaces.ITaskDTO;
import com.todo.business.service.interfaces.IGetTaskByIdApplicationService;
import com.todo.repository.entity.Task;
import com.todo.repository.mapper.interfaces.IMapTaskToTaskDTO;
import com.todo.repository.service.interfaces.ITaskJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

public class GetTaskByIdApplicationService implements IGetTaskByIdApplicationService {
    private static final Logger logger = LoggerFactory.getLogger(GetTaskByIdApplicationService.class);

    @Inject
    private ITaskJpaRepository jpaRepository;
    @Inject
    private IMapTaskToTaskDTO mapper;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            cacheNames = "tasks",
            key = "#id.toString()",
            condition = "#id != '3'",
            unless = "#result == null")
    public ITaskDTO execute(String id) {
        logger.info("Executing {}", this.getClass().getSimpleName());
        Task task = jpaRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new RuntimeException("Task not found"));
        if ("DE".equals(task.getActivationStatus())) {
            throw new RuntimeException("Task is deleted!");
        }
        return mapper.reverseMap(task);
    }
}
