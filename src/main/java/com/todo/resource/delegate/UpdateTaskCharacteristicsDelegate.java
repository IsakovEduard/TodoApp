package com.todo.resource.delegate;

import com.todo.business.model.implementation.Characteristic;
import com.todo.business.model.implementation.PatchElement;
import com.todo.business.model.interfaces.ITask;
import com.todo.business.service.interfaces.IUpdateTaskCharacteristicsApplicationService;
import com.todo.model.V1Characteristic;
import com.todo.model.V1Task;
import com.todo.model.V1UpdateCharacteristicsInput;
import com.todo.resource.mapper.interfaces.IMapITaskToV1Task;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.util.CollectionUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class UpdateTaskCharacteristicsDelegate {
    private static final Logger logger = LoggerFactory.getLogger(UpdateTaskCharacteristicsDelegate.class);


    @Inject
    private ObjectProvider<PatchElement> patchElementObjectProvider;
    @Inject
    private ObjectProvider<Characteristic> characteristicObjectProvider;
    @Inject
    private IUpdateTaskCharacteristicsApplicationService updateTaskCharacteristicsApplicationService;
    @Inject
    private IMapITaskToV1Task mapITaskToV1Task;

    public List<V1Task> execute(String userId, V1UpdateCharacteristicsInput v1UpdateCharacteristicsInput) {

        logger.info("In UpdateTaskCharacteristicsDelegate with input: userId: {}, v1UpdateCharacteristicsInput: {}", userId, v1UpdateCharacteristicsInput);
        PatchElement patchElement = mapInput(v1UpdateCharacteristicsInput);
        ITask updatedTask = updateTaskCharacteristicsApplicationService.execute(userId, patchElement);
        return List.of(mapITaskToV1Task.mapSingle(updatedTask));
    }

    private PatchElement mapInput(V1UpdateCharacteristicsInput v1UpdateCharacteristicsInput) {
        PatchElement patchElement = patchElementObjectProvider.getObject();

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
            Characteristic characteristic = characteristicObjectProvider.getObject();
            characteristic.setName(v1Characteristic.getName());
            characteristic.setValue(v1Characteristic.getValue());
            characteristics.add(characteristic);
        });
        return characteristics;
    }
}
