package com.todo.todo.controller;

import com.todo.todo.business.model.interfaces.ITask;
import com.todo.todo.delegate.AddTaskDelegate;
import com.todo.todo.delegate.GetAllTasksDelegate;
import com.todo.todo.delegate.HelloDelegate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;


@RestController
public class TodoController {

    @Inject
    private HelloDelegate helloDelegate;
    @Inject
    private AddTaskDelegate addTaskDelegate;
    @Inject
    private GetAllTasksDelegate getAllTasksDelegate;

    @GetMapping("/hello")
    public String sayHello() {
        return helloDelegate.sayHello();
    }

    @PostMapping("/createTask")
    public void addTask(@RequestBody ITask task) {
        addTaskDelegate.addTask(task);
    }

    @GetMapping("/tasks")
    public List<ITask> getAllTasks(){
        return getAllTasksDelegate.getAllTasks();
    }
}
