package com.todo.business.service.implementation;

import com.todo.business.service.interfaces.IDeleteTasksByIdsApplicationService;
import com.todo.business.service.interfaces.IGetUserServiceFromContextService;
import com.todo.repository.entity.User;
import com.todo.repository.service.interfaces.ITaskJpaRepository;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

public class DeleteTasksByIdsApplicationService implements IDeleteTasksByIdsApplicationService {

    @Inject
    private ITaskJpaRepository taskJpaRepository;
    @Inject
    private IGetUserServiceFromContextService getUserServiceFromContextService;
    @Inject
    private CacheManager cacheManager;

    @Override
    @Transactional
    public int execute(List<String> taskIds) {

        // 1. Mark tasks as deleted in DB
        User user = getUserServiceFromContextService.getUserFromContext();
        List<Long> ids = taskIds.stream().map(Long::valueOf).toList();
        int deletedTasks = taskJpaRepository.deactivateAndCancelTasks(user.getId(), ids);
        // 2. Evict only affected cache entries
        Cache cache = cacheManager.getCache("tasks");
        if (cache != null) {
            taskIds.forEach(cache::evict);
        }
        return deletedTasks;
    }
}
