package com.todo.business.cache;

import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

public final class TaskCacheKeys {

    private TaskCacheKeys() {
    }

    public static List<String> of(String username, String taskId) {
        return List.of(username, taskId);
    }

    public static List<String> forCurrentUser(String taskId) {
        return of(SecurityContextHolder.getContext().getAuthentication().getName(), taskId);
    }
}
