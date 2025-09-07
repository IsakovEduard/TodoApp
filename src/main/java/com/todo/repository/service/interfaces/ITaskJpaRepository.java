package com.todo.repository.service.interfaces;

import com.todo.repository.DTO.TaskDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ITaskJpaRepository extends JpaRepository<TaskDTO, Long> {
    List<TaskDTO> findByUserId(String userId);
}
