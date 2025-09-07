package com.todo.resource.mapper.implementation;

import com.todo.business.model.interfaces.ITask;
import com.todo.model.V1ActivationStatus;
import com.todo.model.V1Status;
import com.todo.model.V1Task;
import com.todo.model.V1Urgency;
import com.todo.resource.mapper.interfaces.IMapITaskToV1Task;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class MapITaskToV1Task implements IMapITaskToV1Task {

    @Override
    public List<V1Task> map(List<ITask> tasks) {
        List<V1Task> v1Tasks = new ArrayList<>();
        if (!CollectionUtils.isEmpty(tasks)) {
            tasks.forEach(task -> v1Tasks.add(mapSingle(task)));
        }
        return v1Tasks;
    }

    @Override
    public V1Task mapSingle(ITask task) {
        V1Task v1Task = new V1Task();
        v1Task.setId(task.getId().toString());
        v1Task.setUserId(task.getUserId());
        v1Task.setTitle(task.getTitle());
        v1Task.setDescription(task.getDescription());
        v1Task.setDueDate(task.getDueDate());
        v1Task.setUrgency(V1Urgency.fromValue(task.getUrgency()));
        v1Task.setCreatedAt(task.getCreatedAt());
        v1Task.setStatus(V1Status.fromValue(task.getStatus()));
        v1Task.setActivationStatus(V1ActivationStatus.fromValue(task.getActivationStatus()));

        return v1Task;
    }
}
