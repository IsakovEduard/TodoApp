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
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;

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
                case "DUE_DATE" -> task.setDueDate(newValue == null || newValue.isBlank()
                        ? null
                        : parseDateTime(characteristicName, newValue));
                case "CREATED_AT" -> task.setCreatedAt(parseDateTime(characteristicName, newValue));
                case "URGENCY" -> task.setUrgency(requireAllowed(characteristicName, newValue, TaskConstants.URGENCY_VALUES));
                case "STATUS" -> task.setStatus(requireAllowed(characteristicName, newValue, TaskConstants.STATUS_VALUES));
                default -> throw new IllegalArgumentException(characteristicName + " is not valid value for update!");
            }
        });
    }

    private static LocalDateTime parseDateTime(String field, String value) {
        try {
            return LocalDateTime.parse(Objects.requireNonNullElse(value, ""));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(field + " must be an ISO date-time, for example 2026-12-01T09:30");
        }
    }

    private static String requireAllowed(String field, String value, List<String> allowedValues) {
        if (value == null || !allowedValues.contains(value)) {
            throw new IllegalArgumentException(field + " must be one of " + allowedValues);
        }
        return value;
    }
}
