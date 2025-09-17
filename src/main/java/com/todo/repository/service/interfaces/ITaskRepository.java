package com.todo.repository.service.interfaces;

import com.todo.business.model.interfaces.ITask;
import com.todo.repository.DTO.TaskDTO;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface ITaskRepository {

    TaskDTO createTask(ITask task);

    List<TaskDTO> getTasksByUser(String userId, boolean includeDeleted);

    TaskDTO getTaskByUserIdAndTaskId(String userId, Long taskId);
}
