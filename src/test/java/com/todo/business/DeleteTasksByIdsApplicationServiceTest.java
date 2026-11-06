package com.todo.business;

import com.todo.business.service.implementation.DeleteTasksByIdsApplicationService;
import com.todo.business.service.interfaces.IGetUserFromContextService;
import com.todo.repository.entity.User;
import com.todo.repository.service.interfaces.ITaskJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DeleteTasksByIdsApplicationServiceTest {

    private static final long USER_ID = 1L;

    @Mock
    private ITaskJpaRepository taskJpaRepository;
    @Mock
    private CacheManager cacheManager;
    @Mock
    private Cache cache;
    @Mock
    private IGetUserFromContextService getUserFromContextService;

    @InjectMocks
    private DeleteTasksByIdsApplicationService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        User mockUser = new User();
        mockUser.setId(USER_ID);
        mockUser.setUsername("testUser");
        when(getUserFromContextService.getUserFromContext()).thenReturn(mockUser);
        when(cacheManager.getCache("tasks")).thenReturn(cache);
    }

    @Test
    void execute_ShouldConvertIdsAndCallRepository() {
        List<String> taskIds = List.of("1", "2", "3");
        List<Long> expectedConvertedIds = List.of(1L, 2L, 3L);
        when(taskJpaRepository.deactivateAndCancelTasks(USER_ID, expectedConvertedIds)).thenReturn(3);

        int result = service.execute(taskIds);

        assertEquals(3, result);
        verify(taskJpaRepository).deactivateAndCancelTasks(USER_ID, expectedConvertedIds);
    }

    @Test
    void execute_ShouldReturnZero_WhenRepositoryReturnsZero() {
        when(taskJpaRepository.deactivateAndCancelTasks(USER_ID, List.of(100L))).thenReturn(0);

        int result = service.execute(List.of("100"));

        assertEquals(0, result);
        verify(taskJpaRepository).deactivateAndCancelTasks(USER_ID, List.of(100L));
    }

    @Test
    void execute_ShouldEvictDeletedTasksFromCache() {
        List<String> taskIds = List.of("1", "2");
        when(taskJpaRepository.deactivateAndCancelTasks(USER_ID, List.of(1L, 2L))).thenReturn(2);

        service.execute(taskIds);

        verify(cache).evict("1");
        verify(cache).evict("2");
    }
}
