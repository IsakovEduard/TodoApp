package com.todo.business;

import com.todo.business.model.implementation.Task;
import com.todo.business.model.interfaces.ITask;
import com.todo.business.service.implementation.CreateTaskApplicationService;
import com.todo.repository.DTO.TaskDTO;
import com.todo.repository.mapper.interfaces.IMapTaskToTaskDTO;
import com.todo.repository.service.interfaces.ITaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateTaskApplicationServiceTest {

    @Mock
    private ITaskRepository taskRepository;

    @Mock
    private IMapTaskToTaskDTO mapper;

    @InjectMocks
    private CreateTaskApplicationService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void execute_ShouldSetDefaultFieldsAndReturnMappedTask() {
        // Arrange
        ITask inputTask = mock(ITask.class);
        ITask mappedTask = mock(ITask.class);
        TaskDTO createdTask = mock(TaskDTO.class);

        when(taskRepository.createTask(inputTask)).thenReturn(createdTask);
        when(mapper.reverseMap(createdTask)).thenReturn(mappedTask);

        // Act
        ITask result = service.execute(inputTask);

        // Assert
        assertSame(mappedTask, result); // the returned task should be the mapped task
        verify(inputTask).setCreatedAt(any(LocalDateTime.class));
        verify(inputTask).setStatus("IN_PROGRESS");
        verify(inputTask).setActivationStatus("AC");

        verify(taskRepository, times(1)).createTask(inputTask);
        verify(mapper, times(1)).reverseMap(createdTask);
    }

    @Test
    void execute_ShouldCallRepositoryAndMapper() {
        // Arrange
        ITask inputTask = mock(ITask.class);
        ITask mappedTask = mock(ITask.class);
        TaskDTO createdTask = mock(TaskDTO.class);

        when(taskRepository.createTask(inputTask)).thenReturn(createdTask);
        when(mapper.reverseMap(createdTask)).thenReturn(mappedTask);

        // Act
        ITask result = service.execute(inputTask);

        // Assert
        verify(taskRepository).createTask(inputTask);
        verify(mapper).reverseMap(createdTask);
    }
}