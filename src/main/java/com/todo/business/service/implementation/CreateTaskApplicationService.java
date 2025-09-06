package com.todo.business.service.implementation;

import com.todo.business.model.interfaces.ITask;
import com.todo.business.service.interfaces.ICreateTaskApplicationService;
import com.todo.repository.mapper.interfaces.IMapTaskToTaskDTO;
import com.todo.repository.service.interfaces.ITaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.LocalDateTime;

public class CreateTaskApplicationService implements ICreateTaskApplicationService {

    private static final Logger logger = LoggerFactory.getLogger(CreateTaskApplicationService.class);

    @Inject
    private ITaskRepository taskRepository;
    @Inject
    private IMapTaskToTaskDTO mapper;

    @Override
    public ITask execute(ITask task) {
        logger.info("Executing CreateTaskApplicationService with input: {}", task);

        // Populate createdAt for new task
        task.setCreatedAt(LocalDateTime.now());

        return mapper.reverseMap(taskRepository.createTask(task));
    }
}
