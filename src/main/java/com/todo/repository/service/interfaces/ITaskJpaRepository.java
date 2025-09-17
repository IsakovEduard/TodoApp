package com.todo.repository.service.interfaces;

import com.todo.business.model.implementation.Task;
import com.todo.repository.DTO.TaskDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ITaskJpaRepository extends JpaRepository<TaskDTO, Long> {
    List<TaskDTO> findByUserId(String userId);
    Optional<TaskDTO> findByUserIdAndId(String userId, Long taskId);

    @Modifying
    @Transactional
    @Query("""
    UPDATE TaskDTO t
       SET t.activationStatus = 'DE',
           t.status = 'CANCELLED'
     WHERE t.userId = :userId
       AND t.activationStatus = 'AC'
       AND t.id IN :taskIds
    """)
    int deactivateAndCancelTasks(@Param("userId") String userId,
                                 @Param("taskIds") List<Long> taskIds);
}
