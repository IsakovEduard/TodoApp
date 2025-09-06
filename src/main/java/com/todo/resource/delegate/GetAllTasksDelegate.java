package com.todo.resource.delegate;

import com.todo.business.model.interfaces.ITask;
import com.todo.repository.DTO.TaskDTO;
import com.todo.repository.mapper.interfaces.IMapTaskToTaskDTO;
import com.todo.repository.service.interfaces.ITaskJpaRepository;

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
