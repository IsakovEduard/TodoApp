package com.todo.business;

import com.todo.business.model.implementation.Characteristic;
import com.todo.business.model.implementation.PatchElement;
import com.todo.business.model.interfaces.ITaskDTO;
import com.todo.business.service.implementation.UpdateTaskCharacteristicsApplicationService;
import com.todo.business.service.interfaces.IGetUserFromContextService;
import com.todo.repository.entity.Task;
import com.todo.repository.entity.User;
import com.todo.repository.mapper.interfaces.IMapTaskToTaskDTO;
import com.todo.repository.service.interfaces.ITaskJpaRepository;
import com.todo.repository.service.interfaces.ITaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UpdateTaskCharacteristicsApplicationServiceTest {

    private static final long USER_ID = 1L;
    private static final String TASK_ID = "100";

    @Mock
    private ITaskRepository taskRepository;
    @Mock
    private ITaskJpaRepository jpaRepository;
    @Mock
    private IMapTaskToTaskDTO mapTaskToTaskDTO;
    @Mock
    private IGetUserFromContextService getUserFromContextService;

    @InjectMocks
    private UpdateTaskCharacteristicsApplicationService service;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(USER_ID);
        user.setUsername("testUser");
        when(getUserFromContextService.getUserFromContext()).thenReturn(user);
    }

    @Test
    void execute_ShouldUpdateTaskCharacteristicsAndReturnMappedTask() {
        PatchElement patchElement = new PatchElement(TASK_ID, List.of(
                new Characteristic("title", "New Title"),
                new Characteristic("description", "Updated description"),
                new Characteristic("due_date", "2025-12-01T12:00:00"),
                new Characteristic("status", "COMPLETED"),
                new Characteristic("urgency", "HIGH")
        ));

        Task task = new Task();
        task.setId(Long.valueOf(TASK_ID));
        task.setUser(user);
        task.setTitle("Old Title");
        task.setDescription("Old description");
        task.setDueDate(LocalDateTime.parse("2025-11-01T12:00:00"));
        task.setStatus("IN_PROGRESS");
        task.setUrgency("LOW");
        ITaskDTO mappedTask = stubPersistedTask(task);

        ITaskDTO result = service.execute(patchElement);

        assertEquals("New Title", task.getTitle());
        assertEquals("Updated description", task.getDescription());
        assertEquals(LocalDateTime.parse("2025-12-01T12:00:00"), task.getDueDate());
        assertEquals("COMPLETED", task.getStatus());
        assertEquals("HIGH", task.getUrgency());
        assertSame(mappedTask, result);
        verify(jpaRepository).save(task);
    }

    @Test
    void execute_ShouldNotChangeTaskButReturnMappedTask_WhenCharacteristicsEmpty() {
        PatchElement patchElement = new PatchElement(TASK_ID, List.of());

        Task task = new Task();
        task.setId(Long.valueOf(TASK_ID));
        task.setUser(user);
        ITaskDTO mappedTask = stubPersistedTask(task);

        ITaskDTO result = service.execute(patchElement);

        assertSame(mappedTask, result);
        verify(jpaRepository).save(task);
    }

    @Test
    void execute_ShouldThrowIllegalArgumentException_WhenCharacteristicIsNotUpdatable() {
        PatchElement patchElement = new PatchElement(TASK_ID, List.of(new Characteristic("unknown", "value")));
        Task task = new Task();
        task.setId(Long.valueOf(TASK_ID));
        stubPersistedTask(task);

        assertThrows(IllegalArgumentException.class, () -> service.execute(patchElement));
        verify(jpaRepository, never()).save(any());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void execute_ShouldClearDueDate_WhenValueIsBlank(String blankValue) {
        Task task = new Task();
        task.setId(Long.valueOf(TASK_ID));
        task.setDueDate(LocalDateTime.parse("2025-11-01T12:00:00"));
        stubPersistedTask(task);

        service.execute(new PatchElement(TASK_ID, List.of(new Characteristic("due_date", blankValue))));

        assertNull(task.getDueDate());
        verify(jpaRepository).save(task);
    }

    @ParameterizedTest
    @CsvSource({"status,BANANA", "urgency,URGENT", "due_date,tomorrow", "created_at,yesterday"})
    void execute_ShouldThrowIllegalArgumentException_WhenValueIsNotAllowed(String name, String value) {
        Task task = new Task();
        task.setId(Long.valueOf(TASK_ID));
        stubPersistedTask(task);
        PatchElement patchElement = new PatchElement(TASK_ID, List.of(new Characteristic(name, value)));

        assertThrows(IllegalArgumentException.class, () -> service.execute(patchElement));
        verify(jpaRepository, never()).save(any());
    }

    private ITaskDTO stubPersistedTask(Task task) {
        ITaskDTO mappedTask = mock(ITaskDTO.class);
        when(taskRepository.getTaskByUserIdAndTaskId(USER_ID, Long.valueOf(TASK_ID))).thenReturn(task);
        when(jpaRepository.save(task)).thenReturn(task);
        when(mapTaskToTaskDTO.reverseMap(task)).thenReturn(mappedTask);
        return mappedTask;
    }
}
