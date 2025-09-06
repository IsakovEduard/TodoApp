package com.todo.api;

import com.todo.business.model.interfaces.ITask;
import com.todo.model.V1CreateTaskInput;
import com.todo.model.V1Task;
import com.todo.resource.delegate.CreateTaskDelegate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

@RestController
public class TaskControllerImpl implements TaskManagementApi {

    @Inject
    private CreateTaskDelegate createTaskDelegate;


    @Override
    public ResponseEntity<String> createTask(V1CreateTaskInput v1CreateTaskInput) {
        ITask createdTask = createTaskDelegate.addTask(v1CreateTaskInput);
        return ResponseEntity.status(201).body(createdTask.getId().toString());
    }

    @Override
    public ResponseEntity<List<V1Task>> getTasks() {
        return null;
    }
}
