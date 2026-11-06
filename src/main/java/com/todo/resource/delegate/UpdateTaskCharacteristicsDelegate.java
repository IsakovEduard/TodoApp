package com.todo.resource.delegate;

import com.todo.business.model.implementation.Characteristic;
import com.todo.business.model.implementation.PatchElement;
import com.todo.business.model.interfaces.ITaskDTO;
import com.todo.business.service.interfaces.IUpdateTaskCharacteristicsApplicationService;
import com.todo.controller.api.model.V1Characteristic;
import com.todo.controller.api.model.V1Task;
import com.todo.controller.api.model.V1UpdateCharacteristicsInput;
import com.todo.resource.mapper.interfaces.IMapITaskToV1Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
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

    public List<V1Task> execute(V1UpdateCharacteristicsInput v1UpdateCharacteristicsInput) {

        logger.info("In UpdateTaskCharacteristicsDelegate with input: v1UpdateCharacteristicsInput: {}", v1UpdateCharacteristicsInput);
        PatchElement patchElement = mapInput(v1UpdateCharacteristicsInput);
        ITaskDTO updatedTask = updateTaskCharacteristicsApplicationService.execute(patchElement);
        return List.of(mapITaskToV1Task.mapSingle(updatedTask));
    }

    private PatchElement mapInput(V1UpdateCharacteristicsInput v1UpdateCharacteristicsInput) {
        PatchElement patchElement = new PatchElement();

        if (v1UpdateCharacteristicsInput != null && !CollectionUtils.isEmpty(v1UpdateCharacteristicsInput.getCharacteristics())) {
            List<Characteristic> characteristics = mapCharacteristics(v1UpdateCharacteristicsInput);
            patchElement.setChangeCharacteristics(characteristics);
            patchElement.setTaskId(v1UpdateCharacteristicsInput.getTaskId());
        }

        return patchElement;
    }

    private List<Characteristic> mapCharacteristics(V1UpdateCharacteristicsInput v1UpdateCharacteristicsInput) {
        List<Characteristic> characteristics = new ArrayList<>();
        List<V1Characteristic> v1Characteristics = v1UpdateCharacteristicsInput.getCharacteristics();
        v1Characteristics.forEach(v1Characteristic -> {
            Characteristic characteristic = new Characteristic();
            characteristic.setName(v1Characteristic.getName());
            characteristic.setValue(v1Characteristic.getValue());
            characteristics.add(characteristic);
        });
        return characteristics;
    }
}
