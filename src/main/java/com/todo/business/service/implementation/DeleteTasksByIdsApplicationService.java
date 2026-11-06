package com.todo.business.service.implementation;

import com.todo.business.constant.TaskConstants;
import com.todo.business.service.interfaces.IDeleteTasksByIdsApplicationService;
import com.todo.business.service.interfaces.IGetUserFromContextService;
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
    private final IGetUserFromContextService getUserFromContextService;
    private final CacheManager cacheManager;

    public DeleteTasksByIdsApplicationService(ITaskJpaRepository taskJpaRepository,
                                              IGetUserFromContextService getUserFromContextService,
                                              CacheManager cacheManager) {
        this.taskJpaRepository = taskJpaRepository;
        this.getUserFromContextService = getUserFromContextService;
        this.cacheManager = cacheManager;
    }

    @Override
    @Transactional
    public int execute(List<String> taskIds) {
        User user = getUserFromContextService.getUserFromContext();
        List<Long> ids = taskIds.stream().map(Long::valueOf).toList();
        int deletedTasks = taskJpaRepository.deactivateAndCancelTasks(user.getId(), ids);

        Cache cache = cacheManager.getCache(TaskConstants.CACHE_TASKS);
        if (cache != null) {
            taskIds.forEach(cache::evict);
        }
        return deletedTasks;
    }
}
