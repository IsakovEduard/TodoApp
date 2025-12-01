package com.todo.business;

import com.todo.business.model.interfaces.ITaskDTO;
import com.todo.business.service.implementation.CreateTaskApplicationService;
import com.todo.business.service.interfaces.IGetUserServiceFromContextService;
import com.todo.repository.entity.Task;
import com.todo.repository.entity.User;
import com.todo.repository.mapper.interfaces.IMapTaskToTaskDTO;
import com.todo.repository.service.interfaces.ITaskRepository;
import com.todo.repository.service.interfaces.IUserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class CreateTaskApplicationServiceTest {

    @Mock
    private ITaskRepository taskRepository;
    @Mock
    private IUserJpaRepository userJpaRepository;
    @Mock
    private IGetUserServiceFromContextService getUserServiceFromContextService;
    @Mock
    private IMapTaskToTaskDTO mapper;

    @InjectMocks
    private CreateTaskApplicationService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 1) Mock SecurityContext with authenticated user
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken("testUser", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // 2) Mock userJpaRepository to return a valid User
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testUser");
        when(getUserServiceFromContextService.getUserFromContext())
                .thenReturn(mockUser);
    }

    @Test
    void execute_ShouldSetDefaultFieldsAndReturnMappedTask() {
        // Arrange
        ITaskDTO inputTask = mock(ITaskDTO.class);
        ITaskDTO mappedTask = mock(ITaskDTO.class);
        Task createdTask = mock(Task.class);

        when(taskRepository.createTask(inputTask)).thenReturn(createdTask);
        when(mapper.reverseMap(createdTask)).thenReturn(mappedTask);

        // Act
        ITaskDTO result = service.execute(inputTask);

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
        ITaskDTO inputTask = mock(ITaskDTO.class);
        ITaskDTO mappedTask = mock(ITaskDTO.class);
        Task createdTask = mock(Task.class);

        when(taskRepository.createTask(inputTask)).thenReturn(createdTask);
        when(mapper.reverseMap(createdTask)).thenReturn(mappedTask);

        // Act
        ITaskDTO result = service.execute(inputTask);

        // Assert
        verify(taskRepository).createTask(inputTask);
        verify(mapper).reverseMap(createdTask);
    }
}