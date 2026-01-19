package com.todo.business.service.implementation;

import com.todo.business.model.interfaces.ITaskDTO;
import com.todo.business.service.interfaces.IGetTaskByIdApplicationService;
import com.todo.repository.entity.Task;
import com.todo.repository.mapper.interfaces.IMapTaskToTaskDTO;
import com.todo.repository.service.interfaces.ITaskJpaRepository;

import javax.inject.Inject;

public class GetTaskByIdApplicationService implements IGetTaskByIdApplicationService {

    @Inject
    private ITaskJpaRepository jpaRepository;
    @Inject
    private IMapTaskToTaskDTO mapper;

    @Override
    public ITaskDTO execute(String taskId) {
        Task task = jpaRepository.findById(Long.valueOf(taskId))
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return mapper.reverseMap(task);
    }
}
