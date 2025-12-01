package com.todo.business.service.implementation;

import com.todo.business.service.interfaces.IGetUserServiceFromContextService;
import com.todo.repository.entity.User;
import com.todo.repository.service.interfaces.IUserJpaRepository;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.inject.Inject;

public class GetUserServiceFromContextServiceService implements IGetUserServiceFromContextService {

    @Inject
    private IUserJpaRepository userJpaRepository;

    public User getUserFromContext() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userJpaRepository.findByUsername(username).orElseThrow();
    }
}
