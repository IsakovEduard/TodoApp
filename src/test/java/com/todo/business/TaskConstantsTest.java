package com.todo.business;

import com.todo.business.constant.TaskConstants;
import com.todo.controller.api.model.V1Status;
import com.todo.controller.api.model.V1Urgency;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskConstantsTest {

    @Test
    void urgencyValues_ShouldMatchTheOpenApiEnum() {
        Set<String> generated = Arrays.stream(V1Urgency.values()).map(V1Urgency::getValue).collect(Collectors.toSet());

        assertEquals(generated, Set.copyOf(TaskConstants.URGENCY_VALUES));
    }

    @Test
    void statusValues_ShouldMatchTheOpenApiEnum() {
        Set<String> generated = Arrays.stream(V1Status.values()).map(V1Status::getValue).collect(Collectors.toSet());

        assertEquals(generated, Set.copyOf(TaskConstants.STATUS_VALUES));
    }
}
