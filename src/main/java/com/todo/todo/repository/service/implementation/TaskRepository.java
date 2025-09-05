package com.todo.todo.repository.service.implementation;

import com.todo.todo.business.model.interfaces.ITask;
import com.todo.todo.repository.DTO.TaskDTO;
import com.todo.todo.repository.mapper.interfaces.IMapTaskToTaskDTO;
import com.todo.todo.repository.service.interfaces.ITaskJpaRepository;
import com.todo.todo.repository.service.interfaces.ITaskRepository;

import javax.inject.Inject;

public class TaskRepository implements ITaskRepository {

    @Inject
    private ITaskJpaRepository jpaRepository;

    @Inject
    private IMapTaskToTaskDTO mapTaskToTaskDTO;
    @Override
    public TaskDTO createTask(ITask task) {
        TaskDTO taskDTO = mapTaskToTaskDTO.map(task);
        return jpaRepository.save(taskDTO);
    }
}
