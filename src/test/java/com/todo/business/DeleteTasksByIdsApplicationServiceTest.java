package com.todo.business.service.implementation;

import com.todo.repository.service.interfaces.ITaskJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DeleteTasksByIdsApplicationServiceTest {

    @Mock
    private ITaskJpaRepository jpaRepository;

    @InjectMocks
    private DeleteTasksByIdsApplicationService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void execute_ShouldConvertIdsAndCallRepository() {
        // Arrange
        String userId = "user1";
        List<String> taskIds = List.of("1", "2", "3");
        List<Long> expectedConvertedIds = List.of(1L, 2L, 3L);

        when(jpaRepository.deactivateAndCancelTasks(userId, expectedConvertedIds))
                .thenReturn(3);

        // Act
        int result = service.execute(userId, taskIds);

        // Assert
        assertEquals(3, result);
        verify(jpaRepository, times(1))
                .deactivateAndCancelTasks(userId, expectedConvertedIds);
    }

    @Test
    void execute_ShouldReturnZero_WhenRepositoryReturnsZero() {
        String userId = "user1";
        List<String> taskIds = List.of("100");

        when(jpaRepository.deactivateAndCancelTasks(userId, List.of(100L)))
                .thenReturn(0);

        int result = service.execute(userId, taskIds);

        assertEquals(0, result);
        verify(jpaRepository).deactivateAndCancelTasks(userId, List.of(100L));
    }
}
