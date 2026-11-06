package com.todo.business;

import com.todo.business.model.interfaces.ITaskDTO;
import com.todo.business.service.implementation.CreateTaskApplicationService;
import com.todo.business.service.interfaces.IGetUserFromContextService;
import com.todo.repository.entity.Task;
import com.todo.repository.entity.User;
import com.todo.repository.mapper.interfaces.IMapTaskToTaskDTO;
import com.todo.repository.service.interfaces.ITaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class CreateTaskApplicationServiceTest {

    @Mock
    private ITaskRepository taskRepository;
    @Mock
    private IGetUserFromContextService getUserFromContextService;
    @Mock
    private IMapTaskToTaskDTO mapper;

    @InjectMocks
    private CreateTaskApplicationService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testUser");
        when(getUserFromContextService.getUserFromContext()).thenReturn(mockUser);
    }

    @Test
    void execute_ShouldSetDefaultFieldsAndReturnMappedTask() {
        ITaskDTO inputTask = mock(ITaskDTO.class);
        ITaskDTO mappedTask = mock(ITaskDTO.class);
        Task createdTask = mock(Task.class);

        when(taskRepository.createTask(inputTask)).thenReturn(createdTask);
        when(mapper.reverseMap(createdTask)).thenReturn(mappedTask);

        ITaskDTO result = service.execute(inputTask);

        assertSame(mappedTask, result);
        verify(inputTask).setCreatedAt(any(LocalDateTime.class));
        verify(inputTask).setStatus("IN_PROGRESS");
        verify(inputTask).setActivationStatus("AC");
        verify(taskRepository).createTask(inputTask);
        verify(mapper).reverseMap(createdTask);
    }

    @Test
    void execute_ShouldAssignCurrentUserToTask() {
        ITaskDTO inputTask = mock(ITaskDTO.class);
        Task createdTask = mock(Task.class);
        when(taskRepository.createTask(inputTask)).thenReturn(createdTask);
        when(mapper.reverseMap(createdTask)).thenReturn(mock(ITaskDTO.class));

        service.execute(inputTask);

        verify(inputTask).setUser(argThat(user -> user.getId() == 1L));
    }
}
