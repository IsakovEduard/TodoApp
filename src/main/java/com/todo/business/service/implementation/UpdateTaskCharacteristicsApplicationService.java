package com.todo.business.service.implementation;

import com.todo.business.constant.TaskConstants;
import com.todo.business.model.implementation.Characteristic;
import com.todo.business.model.implementation.PatchElement;
import com.todo.business.model.interfaces.ITaskDTO;
import com.todo.business.service.interfaces.IGetUserFromContextService;
import com.todo.business.service.interfaces.IUpdateTaskCharacteristicsApplicationService;
import com.todo.repository.entity.Task;
import com.todo.repository.entity.User;
import com.todo.repository.mapper.interfaces.IMapTaskToTaskDTO;
import com.todo.repository.service.interfaces.ITaskJpaRepository;
import com.todo.repository.service.interfaces.ITaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UpdateTaskCharacteristicsApplicationService implements IUpdateTaskCharacteristicsApplicationService {

    private static final Logger logger = LoggerFactory.getLogger(UpdateTaskCharacteristicsApplicationService.class);

    private final ITaskRepository taskRepository;
    private final ITaskJpaRepository taskJpaRepository;
    private final IMapTaskToTaskDTO mapTaskToTaskDTO;
    private final IGetUserFromContextService getUserFromContextService;

    public UpdateTaskCharacteristicsApplicationService(ITaskRepository taskRepository,
                                                       ITaskJpaRepository taskJpaRepository,
                                                       IMapTaskToTaskDTO mapTaskToTaskDTO,
                                                       IGetUserFromContextService getUserFromContextService) {
        this.taskRepository = taskRepository;
        this.taskJpaRepository = taskJpaRepository;
        this.mapTaskToTaskDTO = mapTaskToTaskDTO;
        this.getUserFromContextService = getUserFromContextService;
    }

    @Override
    @CachePut(
            cacheNames = TaskConstants.CACHE_TASKS,
            key = "T(com.todo.business.cache.TaskCacheKeys).forCurrentUser(#patchElement.taskId())",
            unless = "#result == null"
    )
    public ITaskDTO execute(PatchElement patchElement) {
        logger.info("Updating task id={}", patchElement.taskId());

        User user = getUserFromContextService.getUserFromContext();
        Task task = taskRepository.getTaskByUserIdAndTaskId(user.getId(), Long.valueOf(patchElement.taskId()));
        updateTask(patchElement.changeCharacteristics(), task);

        return mapTaskToTaskDTO.reverseMap(taskJpaRepository.save(task));
    }

    private void updateTask(List<Characteristic> changeCharacteristics, Task task) {
        changeCharacteristics.forEach(characteristic -> {
            String characteristicName = characteristic.name().toUpperCase();
            String newValue = characteristic.value();
            switch (characteristicName) {
                case "TITLE" -> task.setTitle(newValue);
                case "DESCRIPTION" -> task.setDescription(newValue);
                case "DUE_DATE" -> task.setDueDate(LocalDateTime.parse(newValue));
                case "CREATED_AT" -> task.setCreatedAt(LocalDateTime.parse(newValue));
                case "URGENCY" -> task.setUrgency(newValue);
                case "STATUS" -> task.setStatus(newValue);
                default -> throw new IllegalArgumentException(characteristicName + " is not valid value for update!");
            }
        });
    }
}
