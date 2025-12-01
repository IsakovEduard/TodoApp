package com.todo.business.domain.interfaces;

import com.todo.business.model.implementation.Filter;
import com.todo.repository.entity.Task;

import java.util.List;

public interface ITaskFilterDomainService {
    List<Task> applyFilter(List<Task> tasks, Filter filter);
}
