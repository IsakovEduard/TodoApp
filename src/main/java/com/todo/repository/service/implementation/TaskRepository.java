package com.todo.repository.service.implementation;

import com.todo.business.model.interfaces.ITask;
import com.todo.repository.DTO.TaskDTO;
import com.todo.repository.mapper.interfaces.IMapTaskToTaskDTO;
import com.todo.repository.service.interfaces.ITaskJpaRepository;
import com.todo.repository.service.interfaces.ITaskRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public List<TaskDTO> getTasksByUser(String userId, boolean includeDeleted) {
        List<TaskDTO> userTasks = jpaRepository.findByUserId(userId);
        if (!includeDeleted) {
            userTasks = userTasks.stream().filter(taskDTO -> !"DE".equals(taskDTO.getActivationStatus())).toList();
        }
        return userTasks;
    }

//    @Query("SELECT t FROM Task t WHERE t.userId = :userId AND (:status IS NULL OR t.status = :status)")
}
