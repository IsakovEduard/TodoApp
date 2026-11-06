package com.todo.business;

import com.todo.business.config.CacheConfig;
import com.todo.business.model.interfaces.ITaskDTO;
import com.todo.business.service.implementation.GetTaskByIdApplicationService;
import com.todo.business.service.interfaces.IGetTaskByIdApplicationService;
import com.todo.repository.entity.Task;
import com.todo.repository.mapper.interfaces.IMapTaskToTaskDTO;
import com.todo.repository.service.interfaces.ITaskJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringJUnitConfig({CacheConfig.class, GetTaskByIdApplicationService.class})
class GetTaskByIdApplicationServiceCachingTest {

    @MockitoBean
    private ITaskJpaRepository jpaRepository;
    @MockitoBean
    private IMapTaskToTaskDTO mapper;

    @Autowired
    private IGetTaskByIdApplicationService service;

    @ParameterizedTest
    @ValueSource(strings = {"3", "4"})
    void execute_ShouldCacheResult_ForAnyTaskId(String taskId) {
        Task task = new Task();
        task.setActivationStatus("AC");
        ITaskDTO mappedTask = mock(ITaskDTO.class);
        when(jpaRepository.findById(Long.valueOf(taskId))).thenReturn(Optional.of(task));
        when(mapper.reverseMap(task)).thenReturn(mappedTask);

        ITaskDTO first = service.execute(taskId);
        ITaskDTO second = service.execute(taskId);

        assertSame(mappedTask, first);
        assertSame(mappedTask, second);
        verify(jpaRepository, times(1)).findById(Long.valueOf(taskId));
    }

    @Test
    void execute_ShouldThrow_WhenTaskIsDeleted() {
        Task task = new Task();
        task.setActivationStatus("DE");
        when(jpaRepository.findById(9L)).thenReturn(Optional.of(task));

        assertThrows(RuntimeException.class, () -> service.execute("9"));
        verify(mapper, never()).reverseMap(any());
    }
}
