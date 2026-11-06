package com.todo.ui;

public final class UiRoutes {

    public static final String LOGIN = "/login";
    public static final String REGISTER = "/register";
    public static final String TASKS = "/tasks";
    public static final String TASKS_NESTED = "/tasks/**";

    public static final String[] PUBLIC_PATHS = {
            "/", "/index.html", "/assets/**", "/favicon.*",
            LOGIN, REGISTER, TASKS, TASKS_NESTED
    };

    private UiRoutes() {
    }
}
