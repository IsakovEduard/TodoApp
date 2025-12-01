package com.todo.business.service.implementation;

import com.todo.business.service.interfaces.IDeleteTasksByIdsApplicationService;
import com.todo.business.service.interfaces.IGetUserServiceFromContextService;
import com.todo.repository.entity.User;
import com.todo.repository.service.interfaces.ITaskJpaRepository;
import com.todo.repository.service.interfaces.IUserJpaRepository;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.inject.Inject;
import java.util.List;

public class DeleteTasksByIdsApplicationService implements IDeleteTasksByIdsApplicationService {

    @Inject
    private ITaskJpaRepository taskJpaRepository;
    @Inject
    private IGetUserServiceFromContextService getUserServiceFromContextService;
    @Override
    public int execute(List<String> taskIds) {
        User user = getUserServiceFromContextService.getUserFromContext();
        List<Long> ids = taskIds.stream().map(Long::valueOf).toList();
        return taskJpaRepository.deactivateAndCancelTasks(user.getId(), ids);
    }
}
