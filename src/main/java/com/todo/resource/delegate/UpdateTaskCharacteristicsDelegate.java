package com.todo.resource.delegate;

import com.todo.business.model.implementation.Characteristic;
import com.todo.business.model.implementation.PatchElement;
import com.todo.business.model.interfaces.ITaskDTO;
import com.todo.business.service.interfaces.IUpdateTaskCharacteristicsApplicationService;
import com.todo.controller.api.model.V1Task;
import com.todo.controller.api.model.V1UpdateCharacteristicsInput;
import com.todo.resource.mapper.interfaces.IMapITaskToV1Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class UpdateTaskCharacteristicsDelegate {
    private static final Logger logger = LoggerFactory.getLogger(UpdateTaskCharacteristicsDelegate.class);

    private final IUpdateTaskCharacteristicsApplicationService updateTaskCharacteristicsApplicationService;
    private final IMapITaskToV1Task mapITaskToV1Task;

    public UpdateTaskCharacteristicsDelegate(IUpdateTaskCharacteristicsApplicationService updateTaskCharacteristicsApplicationService,
                                             IMapITaskToV1Task mapITaskToV1Task) {
        this.updateTaskCharacteristicsApplicationService = updateTaskCharacteristicsApplicationService;
        this.mapITaskToV1Task = mapITaskToV1Task;
    }

    public List<V1Task> execute(V1UpdateCharacteristicsInput input) {
        logger.info("Updating task characteristics: taskId={}", input == null ? null : input.getTaskId());
        ITaskDTO updatedTask = updateTaskCharacteristicsApplicationService.execute(mapInput(input));
        return List.of(mapITaskToV1Task.mapSingle(updatedTask));
    }

    private PatchElement mapInput(V1UpdateCharacteristicsInput input) {
        if (input == null || CollectionUtils.isEmpty(input.getCharacteristics())) {
            return new PatchElement(null, null);
        }
        List<Characteristic> characteristics = input.getCharacteristics().stream()
                .map(v1Characteristic -> new Characteristic(v1Characteristic.getName(), v1Characteristic.getValue()))
                .toList();
        return new PatchElement(input.getTaskId(), characteristics);
    }
}
