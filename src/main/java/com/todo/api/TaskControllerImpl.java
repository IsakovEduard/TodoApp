package com.todo.api;

import com.todo.business.model.interfaces.ITask;
import com.todo.model.*;
import com.todo.resource.delegate.CreateTaskDelegate;
import com.todo.resource.delegate.DeleteTasksByIdsDelegate;
import com.todo.resource.delegate.GetTasksByFilterDelegate;
import com.todo.resource.delegate.UpdateTaskCharacteristicsDelegate;
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

    @Override
    public ResponseEntity<String> createTask(String userId, V1CreateTaskInput v1CreateTaskInput) {
        ITask createdTask = createTaskDelegate.addTask(userId, v1CreateTaskInput);
        return ResponseEntity.status(201).body(createdTask.getId().toString());
    }

    @Override
    public ResponseEntity<Integer> deleteTask(String userId, V1DeleteTaskInput v1DeleteTaskInput) {
        int deletedInstances = deleteTasksByIdsDelegate.delete(userId, v1DeleteTaskInput);
        return ResponseEntity.ok().body(deletedInstances);
    }

    @Override
    public ResponseEntity<List<V1Task>> getTasksByFilter(String userId, V1SearchFilter filter) {
        List<V1Task> result = getTasksByFilterDelegate.execute(userId, filter);
        return ResponseEntity.ok().body(result);
    }

    @Override
    public ResponseEntity<List<V1Task>> updateTaskCharacteristics(String userId, V1UpdateCharacteristicsInput v1UpdateCharacteristicsInput) {
        List<V1Task> result = updateTaskCharacteristicsDelegate.execute(userId, v1UpdateCharacteristicsInput);
        return ResponseEntity.ok().body(result);
    }


}
