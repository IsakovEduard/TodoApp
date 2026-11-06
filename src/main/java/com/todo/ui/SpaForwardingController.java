package com.todo.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaForwardingController {

    @GetMapping({UiRoutes.LOGIN, UiRoutes.REGISTER, UiRoutes.TASKS, UiRoutes.TASKS_NESTED})
    public String forwardToIndex() {
        return "forward:/index.html";
    }
}
