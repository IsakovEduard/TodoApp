package com.todo.controller;

import com.todo.business.model.interfaces.ITaskDTO;
import com.todo.controller.api.interfaces.TaskManagementApi;
import com.todo.controller.api.model.*;
import com.todo.resource.delegate.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

@RestController
public class TaskControllerImpl implements TaskManagementApi {

    @Inject
    private CreateTaskDelegate createTaskDelegate;
    @Inject
    private GetTasksByFilterDelegate getTasksByFilterDelegate;
    @Inject
    private DeleteTasksByIdsDelegate deleteTasksByIdsDelegate;
    @Inject
    private UpdateTaskCharacteristicsDelegate updateTaskCharacteristicsDelegate;
    @Inject
    private GetTaskByIdDelegate getTaskIdDelegate;

    @Override
    public ResponseEntity<String> createTask(V1CreateTaskInput v1CreateTaskInput) {
        ITaskDTO createdTask = createTaskDelegate.addTask(v1CreateTaskInput);
        return ResponseEntity.status(201).body(createdTask.getId().toString());
    }

    @Override
    public ResponseEntity<Integer> deleteTask( V1DeleteTaskInput v1DeleteTaskInput) {
        int deletedInstances = deleteTasksByIdsDelegate.delete(v1DeleteTaskInput);
        return ResponseEntity.ok().body(deletedInstances);
    }

    @Override
    public ResponseEntity<V1Task> getTaskById(String taskId) {
        V1Task result = getTaskIdDelegate.execute(taskId);
        return ResponseEntity.ok().body(result);
    }

    @Override
    public ResponseEntity<List<V1Task>> getTasksByFilter(String taskId, String status, String urgency) {
        List<V1Task> result = getTasksByFilterDelegate.execute(taskId, status, urgency);
        return ResponseEntity.ok().body(result);
    }

    @Override
    public ResponseEntity<List<V1Task>> updateTaskCharacteristics(V1UpdateCharacteristicsInput v1UpdateCharacteristicsInput) {
        List<V1Task> result = updateTaskCharacteristicsDelegate.execute(v1UpdateCharacteristicsInput);
        return ResponseEntity.ok().body(result);
    }
}
