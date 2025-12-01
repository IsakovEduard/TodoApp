package com.todo.repository.service.interfaces;

import com.todo.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUserJpaRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}