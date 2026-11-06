package com.todo.business.service.implementation;

import com.todo.business.model.interfaces.ITaskDTO;
import com.todo.business.service.interfaces.ICreateTaskApplicationService;
import com.todo.business.service.interfaces.IGetUserServiceFromContextService;
import com.todo.repository.entity.User;
import com.todo.repository.mapper.interfaces.IMapTaskToTaskDTO;
import com.todo.repository.service.interfaces.ITaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CreateTaskApplicationService implements ICreateTaskApplicationService {

    private static final Logger logger = LoggerFactory.getLogger(CreateTaskApplicationService.class);

    private final ITaskRepository taskRepository;
    private final IMapTaskToTaskDTO mapper;
    private final IGetUserServiceFromContextService getUserServiceFromContextService;

    public CreateTaskApplicationService(ITaskRepository taskRepository,
                                        IMapTaskToTaskDTO mapper,
                                        IGetUserServiceFromContextService getUserServiceFromContextService) {
        this.taskRepository = taskRepository;
        this.mapper = mapper;
        this.getUserServiceFromContextService = getUserServiceFromContextService;
    }

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
