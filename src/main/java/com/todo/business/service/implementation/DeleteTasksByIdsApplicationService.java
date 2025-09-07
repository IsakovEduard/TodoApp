package com.todo.business.service.implementation;

import com.todo.business.service.interfaces.IDeleteTasksByIdsApplicationService;
import com.todo.repository.service.interfaces.ITaskJpaRepository;

import javax.inject.Inject;
import java.util.List;

public class DeleteTasksByIdsApplicationService implements IDeleteTasksByIdsApplicationService {

    @Inject
    private ITaskJpaRepository jpaRepository;
    @Override
    public int execute(String userId, List<String> taskIds) {
        List<Long> ids = taskIds.stream().map(Long::valueOf).toList();
        return jpaRepository.deactivateAndCancelTasks(userId, ids);
    }
}
