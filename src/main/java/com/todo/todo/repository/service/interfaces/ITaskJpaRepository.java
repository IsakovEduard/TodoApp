package com.todo.todo.repository.service.interfaces;

import com.todo.todo.repository.DTO.TaskDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITaskJpaRepository extends JpaRepository<TaskDTO, Long> {
}
