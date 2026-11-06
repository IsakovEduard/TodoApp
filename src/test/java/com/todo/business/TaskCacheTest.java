package com.todo.business;

import com.todo.business.config.CacheConfig;
import com.todo.business.model.implementation.Characteristic;
import com.todo.business.model.implementation.PatchElement;
import com.todo.business.model.interfaces.ITaskDTO;
import com.todo.business.service.implementation.DeleteTasksByIdsApplicationService;
import com.todo.business.service.implementation.GetTaskByIdApplicationService;
import com.todo.business.service.implementation.UpdateTaskCharacteristicsApplicationService;
import com.todo.business.service.interfaces.IDeleteTasksByIdsApplicationService;
import com.todo.business.service.interfaces.IGetTaskByIdApplicationService;
import com.todo.business.service.interfaces.IGetUserFromContextService;
import com.todo.business.service.interfaces.IUpdateTaskCharacteristicsApplicationService;
import com.todo.repository.entity.Task;
import com.todo.repository.entity.User;
import com.todo.repository.mapper.interfaces.IMapTaskToTaskDTO;
import com.todo.repository.service.interfaces.ITaskJpaRepository;
import com.todo.repository.service.interfaces.ITaskRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringJUnitConfig({
        CacheConfig.class,
        GetTaskByIdApplicationService.class,
        UpdateTaskCharacteristicsApplicationService.class,
        DeleteTasksByIdsApplicationService.class
})
class TaskCacheTest {

    private static final long ALICE_ID = 1L;
    private static final long BOB_ID = 2L;

    @MockitoBean
    private ITaskJpaRepository jpaRepository;
    @MockitoBean
    private ITaskRepository taskRepository;
    @MockitoBean
    private IMapTaskToTaskDTO mapper;
    @MockitoBean
    private IGetUserFromContextService getUserFromContextService;

    @Autowired
    private IGetTaskByIdApplicationService getTaskById;
    @Autowired
    private IUpdateTaskCharacteristicsApplicationService updateTask;
    @Autowired
    private IDeleteTasksByIdsApplicationService deleteTasks;
    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        cacheManager.getCache("tasks").clear();
        loginAs(ALICE_ID, "alice");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @ParameterizedTest
    @ValueSource(strings = {"3", "4"})
    void getTaskById_ShouldCacheResult_ForAnyTaskId(String taskId) {
        Task task = activeTask();
        ITaskDTO dto = mock(ITaskDTO.class);
        when(jpaRepository.findByUserIdAndId(ALICE_ID, Long.valueOf(taskId))).thenReturn(Optional.of(task));
        when(mapper.reverseMap(task)).thenReturn(dto);

        ITaskDTO first = getTaskById.execute(taskId);
        ITaskDTO second = getTaskById.execute(taskId);

        assertSame(dto, first);
        assertSame(dto, second);
        verify(jpaRepository, times(1)).findByUserIdAndId(ALICE_ID, Long.valueOf(taskId));
    }

    @Test
    void getTaskById_ShouldNotServeCachedTaskToAnotherUser() {
        Task task = activeTask();
        when(jpaRepository.findByUserIdAndId(ALICE_ID, 5L)).thenReturn(Optional.of(task));
        when(mapper.reverseMap(task)).thenReturn(mock(ITaskDTO.class));
        getTaskById.execute("5");

        loginAs(BOB_ID, "bob");
        when(jpaRepository.findByUserIdAndId(BOB_ID, 5L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> getTaskById.execute("5"));
        verify(jpaRepository).findByUserIdAndId(BOB_ID, 5L);
    }

    @Test
    void getTaskById_ShouldThrow_WhenTaskIsDeleted() {
        Task task = new Task();
        task.setActivationStatus("DE");
        when(jpaRepository.findByUserIdAndId(ALICE_ID, 9L)).thenReturn(Optional.of(task));

        assertThrows(RuntimeException.class, () -> getTaskById.execute("9"));
        verify(mapper, never()).reverseMap(any());
    }

    @Test
    void updateTask_ShouldRefreshCachedTask() {
        Task task = activeTask();
        ITaskDTO original = mock(ITaskDTO.class);
        ITaskDTO updated = mock(ITaskDTO.class);
        when(jpaRepository.findByUserIdAndId(ALICE_ID, 5L)).thenReturn(Optional.of(task));
        when(taskRepository.getTaskByUserIdAndTaskId(ALICE_ID, 5L)).thenReturn(task);
        when(jpaRepository.save(task)).thenReturn(task);
        when(mapper.reverseMap(task)).thenReturn(original, updated);

        getTaskById.execute("5");
        updateTask.execute(new PatchElement("5", List.of(new Characteristic("title", "New title"))));
        ITaskDTO afterUpdate = getTaskById.execute("5");

        assertSame(updated, afterUpdate);
        verify(jpaRepository, times(1)).findByUserIdAndId(ALICE_ID, 5L);
    }

    @Test
    void deleteTasks_ShouldEvictCachedTask() {
        Task task = activeTask();
        when(jpaRepository.findByUserIdAndId(ALICE_ID, 5L)).thenReturn(Optional.of(task));
        when(mapper.reverseMap(task)).thenReturn(mock(ITaskDTO.class));
        when(jpaRepository.deactivateAndCancelTasks(ALICE_ID, List.of(5L))).thenReturn(1);

        getTaskById.execute("5");
        deleteTasks.execute(List.of("5"));
        getTaskById.execute("5");

        verify(jpaRepository, times(2)).findByUserIdAndId(ALICE_ID, 5L);
    }

    private void loginAs(long userId, String username) {
        User user = new User();
        user.setId(userId);
        user.setUsername(username);
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(username, null, List.of()));
        when(getUserFromContextService.getUserFromContext()).thenReturn(user);
    }

    private Task activeTask() {
        Task task = new Task();
        task.setActivationStatus("AC");
        return task;
    }
}
