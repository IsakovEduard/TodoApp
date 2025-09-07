package com.todo.business.service.interfaces;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface IDeleteTasksByIdsApplicationService {
    int execute(String userId, List<String> taskIds);
}
