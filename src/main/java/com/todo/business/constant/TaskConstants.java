package com.todo.business.constant;

import java.util.List;

public final class TaskConstants {

    public static final String CACHE_TASKS = "tasks";

    public static final String ACTIVATION_ACTIVE = "AC";
    public static final String ACTIVATION_DELETED = "DE";

    public static final String STATUS_IN_PROGRESS = "IN_PROGRESS";

    public static final List<String> URGENCY_VALUES = List.of("LOW", "NORMAL", "HIGH");
    public static final List<String> STATUS_VALUES = List.of("IN_PROGRESS", "COMPLETED", "CANCELLED", "SCHEDULED");

    private TaskConstants() {
    }
}
