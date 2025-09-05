package com.todo.todo.delegate;

import com.todo.todo.business.model.interfaces.ITask;
import com.todo.todo.repository.DTO.TaskDTO;
import com.todo.todo.repository.mapper.interfaces.IMapTaskToTaskDTO;
import com.todo.todo.repository.service.interfaces.ITaskJpaRepository;

import javax.inject.Inject;
import java.util.List;

public class GetAllTasksDelegate {


    @Inject
    private ITaskJpaRepository jpaRepository;
    @Inject
    private IMapTaskToTaskDTO mapper;

    public List<ITask> getAllTasks() {
        List<TaskDTO> allTaskDTOs = jpaRepository.findAll();
        return allTaskDTOs.stream().map(task -> mapper.reverseMap(task)).toList();
    }
}
