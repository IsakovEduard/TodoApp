package com.todo.business.service.implementation;

import com.todo.business.constant.TaskConstants;
import com.todo.business.model.interfaces.ITaskDTO;
import com.todo.business.service.interfaces.ICreateTaskApplicationService;
import com.todo.business.service.interfaces.IGetUserFromContextService;
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
    private final IGetUserFromContextService getUserFromContextService;

    public CreateTaskApplicationService(ITaskRepository taskRepository,
                                        IMapTaskToTaskDTO mapper,
                                        IGetUserFromContextService getUserFromContextService) {
        this.taskRepository = taskRepository;
        this.mapper = mapper;
        this.getUserFromContextService = getUserFromContextService;
    }

    @Override
    public ITaskDTO execute(ITaskDTO taskDto) {
        User user = getUserFromContextService.getUserFromContext();
        logger.info("Creating task for userId={}", user.getId());
        taskDto.setUser(user);
        taskDto.setCreatedAt(LocalDateTime.now());
        taskDto.setStatus(TaskConstants.STATUS_IN_PROGRESS);
        taskDto.setActivationStatus(TaskConstants.ACTIVATION_ACTIVE);

        return mapper.reverseMap(taskRepository.createTask(taskDto));
    }
}
