package com.todo.business.service.implementation;

import com.todo.business.model.implementation.Characteristic;
import com.todo.business.model.implementation.PatchElement;
import com.todo.business.model.interfaces.ITask;
import com.todo.business.service.interfaces.IUpdateTaskCharacteristicsApplicationService;
import com.todo.repository.DTO.TaskDTO;
import com.todo.repository.mapper.interfaces.IMapTaskToTaskDTO;
import com.todo.repository.service.interfaces.ITaskJpaRepository;
import com.todo.repository.service.interfaces.ITaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

public class UpdateTaskCharacteristicsApplicationService implements IUpdateTaskCharacteristicsApplicationService     {

    private static final Logger logger = LoggerFactory.getLogger(UpdateTaskCharacteristicsApplicationService.class);

    @Inject
    private ITaskRepository taskRepository;
    @Inject
    private ITaskJpaRepository jpaRepository;
    @Inject
    private IMapTaskToTaskDTO mapTaskToTaskDTO;

    @Override
    public ITask execute(String userId, PatchElement patchElement) {
        logger.info("Executing UpdateTaskCharacteristicsApplicationService with input: {}, {}", userId, patchElement);

        // Step 1: Retrieve relevant tasks
        TaskDTO task = taskRepository.getTaskByUserIdAndTaskId(userId, Long.valueOf(patchElement.getTaskId()));
        // Step 2: update DTO
        updateTaskDTO(patchElement.getChangeCharacteristics(), task);

        // Step 3: save DTO
        TaskDTO save = jpaRepository.save(task);

        return mapTaskToTaskDTO.reverseMap(save);
    }


    private void updateTaskDTO( List<Characteristic> changeCharacteristics, TaskDTO dto) {
        changeCharacteristics.forEach(characteristic -> {
            String charName = characteristic.getName().toUpperCase();
            String newCharValue = characteristic.getValue();
            switch (charName) {
                case "TITLE" -> dto.setTitle(newCharValue);
                case "DESCRIPTION" -> dto.setDescription(newCharValue);
                case "DUE_DATE" -> dto.setDueDate(LocalDateTime.parse(newCharValue));
                case "CREATED_AT" -> dto.setCreatedAt(LocalDateTime.parse(newCharValue));
                case "URGENCY" -> dto.setUrgency(newCharValue);
                case "STATUS" -> dto.setStatus(newCharValue);
            }
        });
    }
}
