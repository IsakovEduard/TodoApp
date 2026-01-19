package com.todo.business;

import com.todo.business.service.implementation.DeleteTasksByIdsApplicationService;
import com.todo.business.service.interfaces.IGetUserServiceFromContextService;
import com.todo.repository.entity.User;
import com.todo.repository.service.interfaces.ITaskJpaRepository;
import com.todo.repository.service.interfaces.IUserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DeleteTasksByIdsApplicationServiceTest {

    @Mock
    private ITaskJpaRepository taskJpaRepository;
    @Mock
    private IUserJpaRepository userJpaRepository;
    @Mock
    private CacheManager cacheManager;
    @Mock
    private Cache cache;
    @Mock
    private IGetUserServiceFromContextService getUserServiceFromContextService;

    @InjectMocks
    private DeleteTasksByIdsApplicationService service;

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

        when(cacheManager.getCache("tasks")).thenReturn(cache);
    }

    @Test
    void execute_ShouldConvertIdsAndCallRepository() {
        // Arrange
        long userId = 1L;
        List<String> taskIds = List.of("1", "2", "3");
        List<Long> expectedConvertedIds = List.of(1L, 2L, 3L);

        when(taskJpaRepository.deactivateAndCancelTasks(userId, expectedConvertedIds))
                .thenReturn(3);

        // Act
        int result = service.execute(taskIds);

        // Assert
        assertEquals(3, result);
        verify(taskJpaRepository, times(1))
                .deactivateAndCancelTasks(userId, expectedConvertedIds);
    }

    @Test
    void execute_ShouldReturnZero_WhenRepositoryReturnsZero() {
        Long userId = 1L;
        List<String> taskIds = List.of("100");

        when(taskJpaRepository.deactivateAndCancelTasks(userId, List.of(100L)))
                .thenReturn(0);

        int result = service.execute(taskIds);

        assertEquals(0, result);
        verify(taskJpaRepository).deactivateAndCancelTasks(userId, List.of(100L));
    }
}
