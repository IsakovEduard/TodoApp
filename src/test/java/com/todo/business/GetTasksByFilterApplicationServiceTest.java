package com.todo.business;

import com.todo.business.domain.interfaces.ITaskFilterDomainService;
import com.todo.business.model.implementation.Filter;
import com.todo.business.model.interfaces.ITask;
import com.todo.business.service.implementation.GetTasksByFilterApplicationService;
import com.todo.repository.DTO.TaskDTO;
import com.todo.repository.mapper.interfaces.IMapTaskToTaskDTO;
import com.todo.repository.service.interfaces.ITaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetTasksByFilterApplicationServiceTest {

    @Mock
    private ITaskRepository taskRepository;

    @Mock
    private IMapTaskToTaskDTO mapper;
    @Mock
    private ITaskFilterDomainService taskFilterDomainService;

    @InjectMocks
    private GetTasksByFilterApplicationService service;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void execute_WhenNoTasksExist_ReturnsEmptyList() {
        String userId = "user1";
        when(taskRepository.getTasksByUser(userId, false)).thenReturn(null);

        List<ITask> result = service.execute(userId, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(taskRepository, times(1)).getTasksByUser(userId, false);
        verifyNoInteractions(mapper);
    }

    @Test
    void execute_WhenTasksExistAndFilterIsNull_ReturnsMappedTasks() {
        String userId = "user1";
        List<TaskDTO> taskDTOList = new ArrayList<>();
        TaskDTO taskDTO = mock(TaskDTO.class);
        taskDTOList.add(taskDTO);

        ITask task = mock(ITask.class);

        when(taskRepository.getTasksByUser(userId, false)).thenReturn(taskDTOList);
        when(mapper.reverseMap(taskDTO)).thenReturn(task);

        List<ITask> result = service.execute(userId, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(task, result.get(0));

        verify(taskRepository, times(1)).getTasksByUser(userId, false);
        verify(mapper, times(1)).reverseMap(taskDTO);
    }

    @Test
    void execute_WhenTasksExistAndFilterIsNotNull_CallsApplyFilters() {
        String userId = "user1";
        List<TaskDTO> taskDTOList = new ArrayList<>();
        TaskDTO taskDTO = mock(TaskDTO.class);
        taskDTOList.add(taskDTO);

        ITask task = mock(ITask.class);
        Filter filter = new Filter(); // can set fields if needed

        when(taskRepository.getTasksByUser(userId, false)).thenReturn(taskDTOList);
        when(taskFilterDomainService.applyFilter(taskDTOList, filter)).thenReturn(taskDTOList); // mock filter
        when(mapper.reverseMap(taskDTO)).thenReturn(task);

        List<ITask> result = service.execute(userId, filter);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(task, result.get(0));

        // verify interactions
        verify(taskRepository, times(1)).getTasksByUser(userId, false);
        verify(taskFilterDomainService, times(1)).applyFilter(taskDTOList, filter);
        verify(mapper, times(1)).reverseMap(taskDTO);
    }
}