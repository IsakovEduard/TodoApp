package com.todo.resource.delegate;

import com.todo.business.model.implementation.TaskDTO;
import com.todo.business.model.interfaces.ITaskDTO;
import com.todo.business.service.interfaces.ICreateTaskApplicationService;

import com.todo.controller.api.model.V1CreateTaskInput;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CreateTaskDelegate {

    private static final Logger logger = LoggerFactory.getLogger(CreateTaskDelegate.class);

    private final ICreateTaskApplicationService createTaskApplicationService;

    public CreateTaskDelegate(ICreateTaskApplicationService createTaskApplicationService) {
        this.createTaskApplicationService = createTaskApplicationService;
    }

    public ITaskDTO addTask(V1CreateTaskInput input) {
        validateInput(input);
        ITaskDTO result = createTaskApplicationService.execute(mapInput(input));
        logger.info("Task created: id={}", result.getId());
        return result;
    }

    private void validateInput(V1CreateTaskInput input) {
        StringBuilder errorMessage = new StringBuilder();
        if (StringUtils.isEmpty(input.getTitle())) {
            errorMessage.append("Title can not be empty. ");
        }
        if (input.getUrgency() == null) {
            errorMessage.append("Urgency value is null or not valid.");
        }

        if (!errorMessage.isEmpty()) {
            throw new IllegalArgumentException(errorMessage.toString());
        }
    }

    private ITaskDTO mapInput(V1CreateTaskInput input) {
        ITaskDTO task = new TaskDTO();
        task.setTitle(input.getTitle());
        task.setDescription(input.getDescription());
        task.setUrgency(input.getUrgency().getValue());
        task.setDueDate(input.getDueDate());
        return task;
    }
}
