package com.todo.business.service.implementation;

import com.todo.business.model.implementation.Characteristic;
import com.todo.business.model.implementation.PatchElement;
import com.todo.business.model.interfaces.ITaskDTO;
import com.todo.business.service.interfaces.IGetUserServiceFromContextService;
import com.todo.business.service.interfaces.IUpdateTaskCharacteristicsApplicationService;
import com.todo.repository.entity.Task;
import com.todo.repository.entity.User;
import com.todo.repository.mapper.interfaces.IMapTaskToTaskDTO;
import com.todo.repository.service.interfaces.ITaskJpaRepository;
import com.todo.repository.service.interfaces.ITaskRepository;
import com.todo.repository.service.interfaces.IUserJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

public class UpdateTaskCharacteristicsApplicationService implements IUpdateTaskCharacteristicsApplicationService     {

    private static final Logger logger = LoggerFactory.getLogger(UpdateTaskCharacteristicsApplicationService.class);

    @Inject
    private ITaskRepository taskRepository;
    @Inject
    private ITaskJpaRepository taskJpaRepository;
    @Inject
    private IMapTaskToTaskDTO mapTaskToTaskDTO;
    @Inject
    private IGetUserServiceFromContextService getUserServiceFromContextService;

    @Override
    public ITaskDTO execute(PatchElement patchElement) {
        logger.info("Executing UpdateTaskCharacteristicsApplicationService with input: {}", patchElement);

        // Step 1: Retrieve relevant tasks
        User user = getUserServiceFromContextService.getUserFromContext();
        Task task = taskRepository.getTaskByUserIdAndTaskId(user.getId(), Long.valueOf(patchElement.getTaskId()));
        // Step 2: update task Entity
        updateTask(patchElement.getChangeCharacteristics(), task);

        // Step 3: save task Entity
        Task save = taskJpaRepository.save(task);

        return mapTaskToTaskDTO.reverseMap(save);
    }


    private void updateTask(List<Characteristic> changeCharacteristics, Task task) {
        changeCharacteristics.forEach(characteristic -> {
            String charName = characteristic.getName().toUpperCase();
            String newCharValue = characteristic.getValue();
            switch (charName) {
                case "TITLE" -> task.setTitle(newCharValue);
                case "DESCRIPTION" -> task.setDescription(newCharValue);
                case "DUE_DATE" -> task.setDueDate(LocalDateTime.parse(newCharValue));
                case "CREATED_AT" -> task.setCreatedAt(LocalDateTime.parse(newCharValue));
                case "URGENCY" -> task.setUrgency(newCharValue);
                case "STATUS" -> task.setStatus(newCharValue);
                default -> throw new IllegalArgumentException(charName + " is not valid value for update!");
            }
        });
    }
}
