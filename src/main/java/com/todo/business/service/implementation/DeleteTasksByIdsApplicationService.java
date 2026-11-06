package com.todo.business.service.implementation;

import com.todo.business.service.interfaces.IDeleteTasksByIdsApplicationService;
import com.todo.business.service.interfaces.IGetUserServiceFromContextService;
import com.todo.repository.entity.User;
import com.todo.repository.service.interfaces.ITaskJpaRepository;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DeleteTasksByIdsApplicationService implements IDeleteTasksByIdsApplicationService {

    private final ITaskJpaRepository taskJpaRepository;
    private final IGetUserServiceFromContextService getUserServiceFromContextService;
    private final CacheManager cacheManager;

    public DeleteTasksByIdsApplicationService(ITaskJpaRepository taskJpaRepository,
                                              IGetUserServiceFromContextService getUserServiceFromContextService,
                                              CacheManager cacheManager) {
        this.taskJpaRepository = taskJpaRepository;
        this.getUserServiceFromContextService = getUserServiceFromContextService;
        this.cacheManager = cacheManager;
    }

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
