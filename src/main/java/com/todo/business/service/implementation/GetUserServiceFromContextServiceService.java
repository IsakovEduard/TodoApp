package com.todo.business.service.implementation;

import com.todo.business.service.interfaces.IGetUserServiceFromContextService;
import com.todo.repository.entity.User;
import com.todo.repository.service.interfaces.IUserJpaRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class GetUserServiceFromContextServiceService implements IGetUserServiceFromContextService {

    private final IUserJpaRepository userJpaRepository;

    public GetUserServiceFromContextServiceService(IUserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public User getUserFromContext() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userJpaRepository.findByUsername(username).orElseThrow();
    }
}
