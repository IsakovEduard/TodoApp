package com.todo.repository.service.interfaces;

import com.todo.repository.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ITaskJpaRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserId(Long userId);
    Optional<Task> findByUserIdAndId(Long userId, Long taskId);
    @Modifying
    @Transactional
    @Query("""
    UPDATE Task t
       SET t.activationStatus = 'DE',
           t.status = 'CANCELLED'
     WHERE t.user.id = :userId
       AND t.activationStatus = 'AC'
       AND t.id IN :taskIds
    """)
    int deactivateAndCancelTasks(@Param("userId") Long userId,
                                 @Param("taskIds") List<Long> taskIds);
}
