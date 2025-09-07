package com.todo.api;

import com.todo.business.model.interfaces.ITask;
import com.todo.model.V1CreateTaskInput;
import com.todo.model.V1DeleteTaskInput;
import com.todo.model.V1SearchFilter;
import com.todo.model.V1Task;
import com.todo.resource.delegate.CreateTaskDelegate;
import com.todo.resource.delegate.GetTasksByFilterDelegate;
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

    @Override
    public ResponseEntity<String> createTask(String userId, V1CreateTaskInput v1CreateTaskInput) {
        ITask createdTask = createTaskDelegate.addTask(userId, v1CreateTaskInput);
        return ResponseEntity.status(201).body(createdTask.getId().toString());
    }

    @Override
    public ResponseEntity<List<String>> deleteTask(String userId, V1DeleteTaskInput v1DeleteTaskInput) {
        return null;
    }

    @Override
    public ResponseEntity<List<V1Task>> getTasksByFilter(String userId, V1SearchFilter filter) {
        List<V1Task> result = getTasksByFilterDelegate.execute(userId, filter);
        return ResponseEntity.ok().body(result);
    }


}
