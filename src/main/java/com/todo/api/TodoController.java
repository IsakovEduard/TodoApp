package com.todo.api;

import com.todo.business.model.interfaces.ITask;
import com.todo.resource.delegate.CreateTaskDelegate;
import com.todo.resource.delegate.GetAllTasksDelegate;
import com.todo.resource.delegate.HelloDelegate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.inject.Inject;
import java.util.List;


//@RestController
public class TodoController {

    @Inject
    private HelloDelegate helloDelegate;
    @Inject
    private CreateTaskDelegate createTaskDelegate;
    @Inject
    private GetAllTasksDelegate getAllTasksDelegate;

    @GetMapping("/hello")
    public String sayHello() {
        return helloDelegate.sayHello();
    }

//    @PostMapping("/createTask")
//    public void addTask(@RequestBody ITask task) {
//        createTaskDelegate.addTask(task);
//    }

    @GetMapping("/tasks")
    public List<ITask> getAllTasks(){
        return getAllTasksDelegate.getAllTasks();
    }
}
