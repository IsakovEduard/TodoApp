package com.todo.business;

import com.todo.business.domain.interfaces.ITaskFilterDomainService;
import com.todo.business.model.implementation.Filter;
import com.todo.business.model.interfaces.ITaskDTO;
import com.todo.business.service.implementation.GetTasksByFilterApplicationService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetTasksByFilterApplicationServiceTest {

    @Mock
    private ITaskRepository taskRepository;
    @Mock
    private IUserJpaRepository userJpaRepository;
    @Mock
    private IMapTaskToTaskDTO mapper;
    @Mock
    private ITaskFilterDomainService taskFilterDomainService;
    @Mock
    private IGetUserServiceFromContextService getUserServiceFromContextService;

    @InjectMocks
    private GetTasksByFilterApplicationService service;


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
    void execute_WhenNoTasksExist_ReturnsEmptyList() {
        long userId = 1L;
        when(taskRepository.getTasksByUser(userId, false)).thenReturn(null);

        List<ITaskDTO> result = service.execute(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(taskRepository, times(1)).getTasksByUser(userId, false);
        verifyNoInteractions(mapper);
    }

    @Test
    void execute_WhenTasksExistAndFilterIsNull_ReturnsMappedTasks() {
        long userId = 1L;
        List<Task> taskList = new ArrayList<>();
        Task taskEntity = mock(Task.class);
        taskList.add(taskEntity);

        ITaskDTO task = mock(ITaskDTO.class);

        when(taskRepository.getTasksByUser(userId, false)).thenReturn(taskList);
        when(mapper.reverseMap(taskEntity)).thenReturn(task);

        List<ITaskDTO> result = service.execute(null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(task, result.get(0));

        verify(taskRepository, times(1)).getTasksByUser(userId, false);
        verify(mapper, times(1)).reverseMap(taskEntity);
    }

    @Test
    void execute_WhenTasksExistAndFilterIsNotNull_CallsApplyFilters() {
        long userId = 1L;
        List<Task> taskList = new ArrayList<>();
        Task taskEntity = mock(Task.class);
        taskList.add(taskEntity);

        ITaskDTO task = mock(ITaskDTO.class);
        Filter filter = new Filter(); // can set fields if needed

        when(taskRepository.getTasksByUser(userId, false)).thenReturn(taskList);
        when(taskFilterDomainService.applyFilter(taskList, filter)).thenReturn(taskList); // mock filter
        when(mapper.reverseMap(taskEntity)).thenReturn(task);

        List<ITaskDTO> result = service.execute(filter);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(task, result.get(0));

        // verify interactions
        verify(taskRepository, times(1)).getTasksByUser(userId, false);
        verify(taskFilterDomainService, times(1)).applyFilter(taskList, filter);
        verify(mapper, times(1)).reverseMap(taskEntity);
    }
}