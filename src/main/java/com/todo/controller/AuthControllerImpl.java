package com.todo.controller;

import com.todo.exception.UnauthorizedException;
import com.todo.repository.entity.User;
import com.todo.security.dto.JwtResponse;
import com.todo.security.dto.LoginRequest;
import com.todo.security.dto.RegisterRequest;
import com.todo.repository.service.interfaces.IUserJpaRepository;
import com.todo.security.jwt.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthControllerImpl {

    private static final String INVALID_CREDENTIALS = "Invalid credentials";

    private final IUserJpaRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthControllerImpl(IUserJpaRepository userRepository,
                              PasswordEncoder passwordEncoder,
                              JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);
        return ResponseEntity.ok("User registered");
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UnauthorizedException(INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException(INVALID_CREDENTIALS);
        }

        String token = jwtUtil.generateToken(user.getUsername());
        return ResponseEntity.ok(new JwtResponse(token));
    }
}