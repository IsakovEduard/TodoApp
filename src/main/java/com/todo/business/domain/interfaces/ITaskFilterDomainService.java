package com.todo.business.domain.interfaces;

import com.todo.business.model.implementation.Filter;
import com.todo.repository.DTO.TaskDTO;

import java.util.List;

public interface ITaskFilterDomainService {
    List<TaskDTO> applyFilter(List<TaskDTO> tasks, Filter filter);
}
