package com.todo.business;

import com.todo.business.model.implementation.Characteristic;
import com.todo.business.model.implementation.PatchElement;
import com.todo.business.model.interfaces.ITask;
import com.todo.business.service.implementation.UpdateTaskCharacteristicsApplicationService;
import com.todo.repository.DTO.TaskDTO;
import com.todo.repository.mapper.interfaces.IMapTaskToTaskDTO;
import com.todo.repository.service.interfaces.ITaskJpaRepository;
import com.todo.repository.service.interfaces.ITaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UpdateTaskCharacteristicsApplicationServiceTest {

    @Mock
    private ITaskRepository taskRepository;

    @Mock
    private ITaskJpaRepository jpaRepository;

    @Mock
    private IMapTaskToTaskDTO mapTaskToTaskDTO;

    @InjectMocks
    private UpdateTaskCharacteristicsApplicationService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void execute_ShouldUpdateTaskCharacteristicsAndReturnMappedTask() {
        String userId = "user1";
        String taskId = "100";

        // Mock input PatchElement
        PatchElement patchElement = new PatchElement();
        patchElement.setTaskId(taskId);
        patchElement.setChangeCharacteristics(List.of(
                new Characteristic("title", "New Title"),
                new Characteristic("description", "Updated description"),
                new Characteristic("due_date", "2025-12-01T12:00:00"),
                new Characteristic("status", "COMPLETED"),
                new Characteristic("urgency", "HIGH")
        ));

        // Mock repository return
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(Long.valueOf(taskId));
        taskDTO.setUserId(userId);
        taskDTO.setTitle("Old Title");
        taskDTO.setDescription("Old description");
        taskDTO.setDueDate(LocalDateTime.parse("2025-11-01T12:00:00"));
        taskDTO.setStatus("IN_PROGRESS");
        taskDTO.setUrgency("LOW");

        when(taskRepository.getTaskByUserIdAndTaskId(userId, Long.valueOf(taskId))).thenReturn(taskDTO);
        when(jpaRepository.save(taskDTO)).thenReturn(taskDTO);

        // Mock mapper return
        ITask mappedTask = mock(ITask.class);
        when(mapTaskToTaskDTO.reverseMap(taskDTO)).thenReturn(mappedTask);

        // Call the service
        ITask result = service.execute(userId, patchElement);

        // Verify updates on DTO
        assertEquals("New Title", taskDTO.getTitle());
        assertEquals("Updated description", taskDTO.getDescription());
        assertEquals(LocalDateTime.parse("2025-12-01T12:00:00"), taskDTO.getDueDate());
        assertEquals("COMPLETED", taskDTO.getStatus());
        assertEquals("HIGH", taskDTO.getUrgency());

        // Verify service returned mapped task
        assertSame(mappedTask, result);

        // Verify interactions
        verify(taskRepository, times(1)).getTaskByUserIdAndTaskId(userId, Long.valueOf(taskId));
        verify(jpaRepository, times(1)).save(taskDTO);
        verify(mapTaskToTaskDTO, times(1)).reverseMap(taskDTO);
    }

    @Test
    void execute_WithEmptyCharacteristics_ShouldNotChangeTaskButReturnMappedTask() {
        String userId = "user1";
        String taskId = "100";

        PatchElement patchElement = new PatchElement();
        patchElement.setTaskId(taskId);
        patchElement.setChangeCharacteristics(List.of()); // empty changes

        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(Long.valueOf(taskId));
        taskDTO.setUserId(userId);

        when(taskRepository.getTaskByUserIdAndTaskId(userId, Long.valueOf(taskId))).thenReturn(taskDTO);
        when(jpaRepository.save(taskDTO)).thenReturn(taskDTO);

        ITask mappedTask = mock(ITask.class);
        when(mapTaskToTaskDTO.reverseMap(taskDTO)).thenReturn(mappedTask);

        ITask result = service.execute(userId, patchElement);

        assertSame(mappedTask, result);
        verify(taskRepository, times(1)).getTaskByUserIdAndTaskId(userId, Long.valueOf(taskId));
        verify(jpaRepository, times(1)).save(taskDTO);
        verify(mapTaskToTaskDTO, times(1)).reverseMap(taskDTO);
    }
}