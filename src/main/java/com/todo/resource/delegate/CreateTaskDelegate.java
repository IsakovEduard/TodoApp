package com.todo.resource.delegate;

import com.todo.business.model.interfaces.ITask;
import com.todo.business.service.interfaces.ICreateTaskApplicationService;

import com.todo.model.V1CreateTaskInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;

import javax.inject.Inject;
import javax.inject.Provider;

public class CreateTaskDelegate {

    private static final Logger logger = LoggerFactory.getLogger(CreateTaskDelegate.class);


    @Inject
    private ICreateTaskApplicationService createTaskApplicationService;
    @Inject
    private ObjectProvider<ITask> taskObjectProvider;
    public ITask addTask(V1CreateTaskInput v1CreateTaskInput) {

        logger.info("Executing AddTaskDelegate: {}", v1CreateTaskInput);
        ITask result = createTaskApplicationService.execute(mapInput(v1CreateTaskInput));
        logger.info("DTO Created: {}", result);
        return result;
    }

    private ITask mapInput(V1CreateTaskInput input) {
        ITask task = taskObjectProvider.getObject();
        task.setTitle(input.getTitle());
        task.setDescription(input.getDescription());
        task.setUrgency(input.getUrgency().getValue());
        task.setDueDate(input.getDueDate());

        return task;
    }
}
