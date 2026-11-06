package com.todo.business;

import com.todo.business.domain.interfaces.ITaskFilterDomainService;
import com.todo.business.model.implementation.Filter;
import com.todo.business.model.interfaces.ITaskDTO;
import com.todo.business.service.implementation.GetTasksByFilterApplicationService;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetTasksByFilterApplicationServiceTest {

    private static final long USER_ID = 1L;

    @Mock
    private ITaskRepository taskRepository;
    @Mock
    private IMapTaskToTaskDTO mapper;
    @Mock
    private ITaskFilterDomainService taskFilterDomainService;
    @Mock
    private IGetUserFromContextService getUserFromContextService;

    @InjectMocks
    private GetTasksByFilterApplicationService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        User mockUser = new User();
        mockUser.setId(USER_ID);
        mockUser.setUsername("testUser");
        when(getUserFromContextService.getUserFromContext()).thenReturn(mockUser);
    }

    @Test
    void execute_ShouldReturnEmptyList_WhenNoTasksExist() {
        when(taskRepository.getTasksByUser(USER_ID, false)).thenReturn(null);

        List<ITaskDTO> result = service.execute(null);

        assertTrue(result.isEmpty());
        verify(taskRepository).getTasksByUser(USER_ID, false);
        verifyNoInteractions(mapper);
    }

    @Test
    void execute_ShouldReturnMappedTasks_WhenFilterIsNull() {
        Task taskEntity = mock(Task.class);
        ITaskDTO task = mock(ITaskDTO.class);
        when(taskRepository.getTasksByUser(USER_ID, false)).thenReturn(List.of(taskEntity));
        when(mapper.reverseMap(taskEntity)).thenReturn(task);

        List<ITaskDTO> result = service.execute(null);

        assertEquals(1, result.size());
        assertSame(task, result.get(0));
        verify(taskRepository).getTasksByUser(USER_ID, false);
        verify(mapper).reverseMap(taskEntity);
        verifyNoInteractions(taskFilterDomainService);
    }

    @Test
    void execute_ShouldApplyFilter_WhenFilterIsNotNull() {
        Task taskEntity = mock(Task.class);
        List<Task> taskList = List.of(taskEntity);
        ITaskDTO task = mock(ITaskDTO.class);
        Filter filter = new Filter(null, null, null);
        when(taskRepository.getTasksByUser(USER_ID, false)).thenReturn(taskList);
        when(taskFilterDomainService.applyFilter(taskList, filter)).thenReturn(taskList);
        when(mapper.reverseMap(taskEntity)).thenReturn(task);

        List<ITaskDTO> result = service.execute(filter);

        assertEquals(1, result.size());
        assertSame(task, result.get(0));
        verify(taskRepository).getTasksByUser(USER_ID, false);
        verify(taskFilterDomainService).applyFilter(taskList, filter);
        verify(mapper).reverseMap(taskEntity);
    }
}
