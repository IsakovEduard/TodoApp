package com.todo.repository.service.implementation;

import com.todo.business.model.interfaces.ITask;
import com.todo.repository.DTO.TaskDTO;
import com.todo.repository.mapper.interfaces.IMapTaskToTaskDTO;
import com.todo.repository.service.interfaces.ITaskJpaRepository;
import com.todo.repository.service.interfaces.ITaskRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

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
            userTasks = filterOutDeleted(userTasks);
        }
        return userTasks;
    }

    @Override
    public TaskDTO getTaskByUserIdAndTaskId(String userId, Long taskId) {
        TaskDTO task = jpaRepository.findByUserIdAndId(userId, taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        if ("DE".equals(task.getActivationStatus())) {
            throw new RuntimeException("Cannot modify deleted task!");
        }
        return task;
    }

    private List<TaskDTO> filterOutDeleted(List<TaskDTO> userTasks) {
        return userTasks.stream().filter(taskDTO -> !"DE".equals(taskDTO.getActivationStatus())).toList();
    }
}
