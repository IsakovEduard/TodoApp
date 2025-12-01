package com.todo.resource.delegate;

import com.todo.business.service.interfaces.IDeleteTasksByIdsApplicationService;
import com.todo.controller.api.model.V1DeleteTaskInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

public class DeleteTasksByIdsDelegate {

    private static final Logger logger = LoggerFactory.getLogger(DeleteTasksByIdsDelegate.class);

    @Inject
    private IDeleteTasksByIdsApplicationService deleteTasksByIdsApplicationService;


    public int delete(V1DeleteTaskInput v1DeleteTaskInput) {
        // TODO: Validate input
        logger.info("In logger with input v1DeleteTaskInput: {}", v1DeleteTaskInput);


        return deleteTasksByIdsApplicationService.execute(v1DeleteTaskInput.getTaskIds());
    }
}
