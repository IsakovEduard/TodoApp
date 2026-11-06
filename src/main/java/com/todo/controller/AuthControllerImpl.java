package com.todo.controller;

import com.todo.business.service.interfaces.IGetUserFromContextService;
import com.todo.controller.api.interfaces.AuthApi;
import com.todo.controller.api.model.V1AuthToken;
import com.todo.controller.api.model.V1Credentials;
import com.todo.controller.api.model.V1CurrentUser;
import com.todo.exception.UnauthorizedException;
import com.todo.repository.entity.User;
import com.todo.repository.service.interfaces.IUserJpaRepository;
import com.todo.security.jwt.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthControllerImpl implements AuthApi {

    private static final String INVALID_CREDENTIALS = "Invalid credentials";

    private final IUserJpaRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final IGetUserFromContextService getUserFromContextService;

    public AuthControllerImpl(IUserJpaRepository userRepository,
                              PasswordEncoder passwordEncoder,
                              JwtUtil jwtUtil,
                              IGetUserFromContextService getUserFromContextService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.getUserFromContextService = getUserFromContextService;
    }

    @Override
    public ResponseEntity<String> register(V1Credentials credentials) {
        if (userRepository.findByUsername(credentials.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        User user = new User();
        user.setUsername(credentials.getUsername());
        user.setPassword(passwordEncoder.encode(credentials.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("User registered");
    }

    @Override
    public ResponseEntity<V1AuthToken> login(V1Credentials credentials) {
        User user = userRepository.findByUsername(credentials.getUsername())
                .orElseThrow(() -> new UnauthorizedException(INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(credentials.getPassword(), user.getPassword())) {
            throw new UnauthorizedException(INVALID_CREDENTIALS);
        }

        return ResponseEntity.ok(new V1AuthToken().token(jwtUtil.generateToken(user.getUsername())));
    }

    @Override
    public ResponseEntity<V1CurrentUser> me() {
        User user = getUserFromContextService.getUserFromContext();
        return ResponseEntity.ok(new V1CurrentUser().username(user.getUsername()));
    }
}
