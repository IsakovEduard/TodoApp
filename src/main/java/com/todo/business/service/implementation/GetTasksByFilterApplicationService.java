package com.todo.business.service.implementation;

import com.todo.business.model.implementation.Filter;
import com.todo.business.model.interfaces.ITask;
import com.todo.business.service.interfaces.IGetTasksByFilterApplicationService;
import com.todo.repository.DTO.TaskDTO;
import com.todo.repository.mapper.interfaces.IMapTaskToTaskDTO;
import com.todo.repository.service.interfaces.ITaskRepository;
import org.springframework.util.CollectionUtils;


import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class GetTasksByFilterApplicationService implements IGetTasksByFilterApplicationService {

    @Inject
    private ITaskRepository taskRepository;
    @Inject
    private IMapTaskToTaskDTO mapper;

    @Override
    public List<ITask> execute(String userId, Filter filter) {

        List<TaskDTO> allTaskDTOs = taskRepository.getTasksByUser(userId, false);
        if (!CollectionUtils.isEmpty(allTaskDTOs) && filter != null) {
            applyFilters(allTaskDTOs, filter);
        }
        if (CollectionUtils.isEmpty(allTaskDTOs)) {
            return new ArrayList<>();
        }
        return allTaskDTOs.stream().map(task -> mapper.reverseMap(task)).toList();
    }

    private void applyFilters(List<TaskDTO> allTaskDTOs, Filter filter) {
        // TODO: Create separate reusable service and implement filtering
    }
}
