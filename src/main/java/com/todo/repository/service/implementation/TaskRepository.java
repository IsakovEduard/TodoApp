package com.todo.repository.service.implementation;

import com.todo.business.model.interfaces.ITaskDTO;
import com.todo.repository.entity.Task;
import com.todo.repository.mapper.interfaces.IMapTaskToTaskDTO;
import com.todo.repository.service.interfaces.ITaskJpaRepository;
import com.todo.repository.service.interfaces.ITaskRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TaskRepository implements ITaskRepository {

    private final ITaskJpaRepository jpaRepository;
    private final IMapTaskToTaskDTO mapTaskToTaskDTO;

    public TaskRepository(ITaskJpaRepository jpaRepository, IMapTaskToTaskDTO mapTaskToTaskDTO) {
        this.jpaRepository = jpaRepository;
        this.mapTaskToTaskDTO = mapTaskToTaskDTO;
    }

    @Override
    public Task createTask(ITaskDTO taskDTO) {
        Task task = mapTaskToTaskDTO.map(taskDTO);
        return jpaRepository.save(task);
    }

    @Override
    public List<Task> getTasksByUser(Long userId, boolean includeDeleted) {
        List<Task> userTasks = jpaRepository.findByUserId(userId);
        if (!includeDeleted) {
            userTasks = filterOutDeleted(userTasks);
        }
        return userTasks;
    }

    @Override
    public Task getTaskByUserIdAndTaskId(Long userId, Long taskId) {
        Task task = jpaRepository.findByUserIdAndId(Long.valueOf(userId), taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        if ("DE".equals(task.getActivationStatus())) {
            throw new RuntimeException("Cannot modify deleted task!");
        }
        return task;
    }

    private List<Task> filterOutDeleted(List<Task> userTasks) {
        return userTasks.stream().filter(taskDTO -> !"DE".equals(taskDTO.getActivationStatus())).toList();
    }
}
