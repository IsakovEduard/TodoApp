package com.todo.resource.delegate;

import com.todo.business.service.interfaces.IDeleteTasksByIdsApplicationService;
import com.todo.controller.api.model.V1DeleteTaskInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import javax.inject.Inject;
import java.util.List;

public class DeleteTasksByIdsDelegate {

    private static final Logger logger = LoggerFactory.getLogger(DeleteTasksByIdsDelegate.class);

    @Inject
    private IDeleteTasksByIdsApplicationService deleteTasksByIdsApplicationService;


    public int delete(V1DeleteTaskInput v1DeleteTaskInput) {
        logger.info("In logger with input v1DeleteTaskInput: {}", v1DeleteTaskInput);

        if (CollectionUtils.isEmpty(v1DeleteTaskInput.getTaskIds())) {
            throw new IllegalArgumentException("Input is empty");
        }
        return deleteTasksByIdsApplicationService.execute(v1DeleteTaskInput.getTaskIds());
    }
}
