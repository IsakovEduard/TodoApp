package com.todo.business;

import com.todo.business.model.implementation.Characteristic;
import com.todo.business.model.implementation.PatchElement;
import com.todo.business.model.interfaces.ITaskDTO;
import com.todo.business.service.implementation.UpdateTaskCharacteristicsApplicationService;
import com.todo.business.service.interfaces.IGetUserServiceFromContextService;
import com.todo.repository.entity.Task;
import com.todo.repository.entity.User;
import com.todo.repository.mapper.interfaces.IMapTaskToTaskDTO;
import com.todo.repository.service.interfaces.ITaskJpaRepository;
import com.todo.repository.service.interfaces.ITaskRepository;
import com.todo.repository.service.interfaces.IUserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.CacheManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UpdateTaskCharacteristicsApplicationServiceTest {

    @Mock
    private ITaskRepository taskRepository;
    @Mock
    private IUserJpaRepository userJpaRepository;
    @Mock
    private CacheManager cacheManager;
    @Mock
    private ITaskJpaRepository jpaRepository;
    @Mock
    private IMapTaskToTaskDTO mapTaskToTaskDTO;
    @Mock
    private IGetUserServiceFromContextService getUserServiceFromContextService;

    @InjectMocks
    private UpdateTaskCharacteristicsApplicationService service;

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
    void execute_ShouldUpdateTaskCharacteristicsAndReturnMappedTask() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
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
        Task task = new Task();
        task.setId(Long.valueOf(taskId));
        task.setUser(user);
        task.setTitle("Old Title");
        task.setDescription("Old description");
        task.setDueDate(LocalDateTime.parse("2025-11-01T12:00:00"));
        task.setStatus("IN_PROGRESS");
        task.setUrgency("LOW");

        when(taskRepository.getTaskByUserIdAndTaskId(user.getId(), Long.valueOf(taskId))).thenReturn(task);
        when(jpaRepository.save(task)).thenReturn(task);

        // Mock mapper return
        ITaskDTO mappedTask = mock(ITaskDTO.class);
        when(mapTaskToTaskDTO.reverseMap(task)).thenReturn(mappedTask);

        // Call the service
        ITaskDTO result = service.execute(patchElement);

        // Verify updates on DTO
        assertEquals("New Title", task.getTitle());
        assertEquals("Updated description", task.getDescription());
        assertEquals(LocalDateTime.parse("2025-12-01T12:00:00"), task.getDueDate());
        assertEquals("COMPLETED", task.getStatus());
        assertEquals("HIGH", task.getUrgency());

        // Verify service returned mapped task
        assertSame(mappedTask, result);

        // Verify interactions
        verify(taskRepository, times(1)).getTaskByUserIdAndTaskId(user.getId(), Long.valueOf(taskId));
        verify(jpaRepository, times(1)).save(task);
        verify(mapTaskToTaskDTO, times(1)).reverseMap(task);
    }

    @Test
    void execute_WithEmptyCharacteristics_ShouldNotChangeTaskButReturnMappedTask() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        String taskId = "100";

        PatchElement patchElement = new PatchElement();
        patchElement.setTaskId(taskId);
        patchElement.setChangeCharacteristics(List.of()); // empty changes

        Task task = new Task();
        task.setId(Long.valueOf(taskId));
        task.setUser(user);

        when(taskRepository.getTaskByUserIdAndTaskId(user.getId(), Long.valueOf(taskId))).thenReturn(task);
        when(jpaRepository.save(task)).thenReturn(task);

        ITaskDTO mappedTask = mock(ITaskDTO.class);
        when(mapTaskToTaskDTO.reverseMap(task)).thenReturn(mappedTask);

        ITaskDTO result = service.execute(patchElement);

        assertSame(mappedTask, result);
        verify(taskRepository, times(1)).getTaskByUserIdAndTaskId(user.getId(), Long.valueOf(taskId));
        verify(jpaRepository, times(1)).save(task);
        verify(mapTaskToTaskDTO, times(1)).reverseMap(task);
    }
}