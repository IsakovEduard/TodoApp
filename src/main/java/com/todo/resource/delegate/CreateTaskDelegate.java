package com.todo.resource.delegate;

import com.todo.business.model.interfaces.ITaskDTO;
import com.todo.business.service.interfaces.ICreateTaskApplicationService;

import com.todo.controller.api.model.V1CreateTaskInput;
import com.todo.controller.api.model.V1Urgency;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;

import javax.inject.Inject;

public class CreateTaskDelegate {

    private static final Logger logger = LoggerFactory.getLogger(CreateTaskDelegate.class);


    @Inject
    private ICreateTaskApplicationService createTaskApplicationService;
    @Inject
    private ObjectProvider<ITaskDTO> taskObjectProvider;
    public ITaskDTO addTask( V1CreateTaskInput v1CreateTaskInput) {
        // TODO: Validate input
        logger.info("Executing AddTaskDelegate: {}", v1CreateTaskInput);
        validateInput(v1CreateTaskInput);
        ITaskDTO result = createTaskApplicationService.execute(mapInput(v1CreateTaskInput));

        logger.info("DTO Created: {}", result);
        return result;
    }

    private void validateInput(V1CreateTaskInput input) {
        StringBuilder errorMessage = new StringBuilder();
        if (StringUtils.isEmpty(input.getTitle())) {
            errorMessage.append("Title can not be empty. ");
        }
        if (StringUtils.isEmpty(input.getDescription())) {
            errorMessage.append("Description can not be empty. ");
        }
        if (StringUtils.isEmpty(input.getUrgency().getValue()) || !EnumUtils.isValidEnum(V1Urgency.class, input.getUrgency().getValue())) {
            errorMessage.append("Urgency value is null or not valid.");
        }

        if (!errorMessage.isEmpty()) {
            throw new IllegalArgumentException(errorMessage.toString());
        }
    }

    private ITaskDTO mapInput(V1CreateTaskInput input) {
        ITaskDTO task = taskObjectProvider.getObject();
        task.setTitle(input.getTitle());
        task.setDescription(input.getDescription());
        task.setUrgency(input.getUrgency().getValue());
        task.setDueDate(input.getDueDate());

        return task;
    }
}
