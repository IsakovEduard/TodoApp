package com.todo.business.service.implementation;

import com.todo.business.model.interfaces.ITaskDTO;
import com.todo.business.service.interfaces.ICreateTaskApplicationService;
import com.todo.business.service.interfaces.IGetUserServiceFromContextService;
import com.todo.repository.entity.User;
import com.todo.repository.mapper.interfaces.IMapTaskToTaskDTO;
import com.todo.repository.service.interfaces.ITaskRepository;
import com.todo.repository.service.interfaces.IUserJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.inject.Inject;
import java.time.LocalDateTime;

public class CreateTaskApplicationService implements ICreateTaskApplicationService {

    private static final Logger logger = LoggerFactory.getLogger(CreateTaskApplicationService.class);

    @Inject
    private ITaskRepository taskRepository;
    @Inject
    private IMapTaskToTaskDTO mapper;
    @Inject
    private IGetUserServiceFromContextService getUserServiceFromContextService;

    @Override
    public ITaskDTO execute(ITaskDTO taskDto) {
        logger.info("Executing CreateTaskApplicationService with input: {}", taskDto);
        User user = getUserServiceFromContextService.getUserFromContext();
        logger.info("user found: {}", user);
        taskDto.setUser(user);
        // Populate fields with default values for new taskDto
        taskDto.setCreatedAt(LocalDateTime.now());
        taskDto.setStatus("IN_PROGRESS");
        taskDto.setActivationStatus("AC");


        return mapper.reverseMap(taskRepository.createTask(taskDto));
    }
}
