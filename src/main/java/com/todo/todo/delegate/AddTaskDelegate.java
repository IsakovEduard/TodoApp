package com.todo.todo.delegate;

import com.todo.todo.business.model.interfaces.ITask;
import com.todo.todo.repository.DTO.TaskDTO;
import com.todo.todo.repository.service.interfaces.ITaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class AddTaskDelegate {

    private static final Logger logger = LoggerFactory.getLogger(AddTaskDelegate.class);

    @Inject
    private ITaskRepository taskRepository;

    public void addTask(ITask task) {
        logger.info("In AddTaskDelegate: {}", task);
        TaskDTO result = taskRepository.createTask(task);
        logger.info("DTO Created: {}", result);
    }
}
